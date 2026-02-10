package com.github.mohrezal.api.domains.users.commands;

import com.github.mohrezal.api.domains.users.commands.params.RefreshTokenCommandParams;
import com.github.mohrezal.api.domains.users.dtos.AuthResponse;
import com.github.mohrezal.api.domains.users.exceptions.types.UserInvalidRefreshTokenException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserRefreshTokenNotFoundException;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.interfaces.Command;
import com.github.mohrezal.api.shared.services.deviceinfo.RequestInfoService;
import com.github.mohrezal.api.shared.services.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenCommand implements Command<RefreshTokenCommandParams, AuthResponse> {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RequestInfoService deviceInfoService;

    @Override
    public void validate(RefreshTokenCommandParams params) {
        if (params.refreshToken() == null || params.refreshToken().isBlank()) {
            throw new UserInvalidRefreshTokenException();
        }
        if (!jwtService.validateRefreshToken(params.refreshToken())) {
            throw new UserInvalidRefreshTokenException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AuthResponse execute(RefreshTokenCommandParams params) {
        try {
            validate(params);

            var refreshTokenEntity =
                    jwtService
                            .getRefreshTokenEntity(params.refreshToken())
                            .orElseThrow(UserRefreshTokenNotFoundException::new);

            var user =
                    userRepository
                            .findById(refreshTokenEntity.getUser().getId())
                            .orElseThrow(UserNotFoundException::new);

            jwtService.revokeRefreshToken(params.refreshToken());

            var newAccessToken = jwtService.generateAccessToken(user);
            var newRefreshToken = jwtService.generateRefreshToken(user.getId());

            var deviceName = deviceInfoService.parseDeviceName(params.userAgent());

            jwtService.saveRefreshToken(
                    newRefreshToken, user, params.ipAddress(), params.userAgent(), deviceName);

            log.info("Token refresh successful for user session");

            return new AuthResponse(newAccessToken, newRefreshToken);

        } catch (UserInvalidRefreshTokenException | UserRefreshTokenNotFoundException ex) {
            log.warn(
                    "Token refresh failed - invalid or expired token. message: {}",
                    ex.getMessage());
            throw ex;
        } catch (UserNotFoundException ex) {
            log.warn("Token refresh failed - user not found. message: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error during token refresh operation", ex);
            throw ex;
        }
    }
}

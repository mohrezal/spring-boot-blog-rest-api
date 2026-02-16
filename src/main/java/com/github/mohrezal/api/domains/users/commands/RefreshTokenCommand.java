package com.github.mohrezal.api.domains.users.commands;

import com.github.mohrezal.api.domains.users.commands.params.RefreshTokenCommandParams;
import com.github.mohrezal.api.domains.users.dtos.AuthResponse;
import com.github.mohrezal.api.domains.users.exceptions.context.UserRefreshTokenExceptionContext;
import com.github.mohrezal.api.domains.users.exceptions.types.UserInvalidRefreshTokenException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserRefreshTokenNotFoundException;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.interfaces.Command;
import com.github.mohrezal.api.shared.services.deviceinfo.RequestInfoService;
import com.github.mohrezal.api.shared.services.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenCommand implements Command<RefreshTokenCommandParams, AuthResponse> {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RequestInfoService deviceInfoService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AuthResponse execute(RefreshTokenCommandParams params) {
        var context =
                new UserRefreshTokenExceptionContext(
                        params.refreshToken() != null && !params.refreshToken().isBlank(),
                        params.ipAddress(),
                        params.userAgent());

        if (!context.hasRefreshToken()) {
            throw new UserInvalidRefreshTokenException(context);
        }

        if (!jwtService.validateRefreshToken(params.refreshToken())) {
            throw new UserInvalidRefreshTokenException(context);
        }

        var refreshTokenEntity =
                jwtService
                        .getRefreshTokenEntity(params.refreshToken())
                        .orElseThrow(() -> new UserRefreshTokenNotFoundException(context));

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
    }
}

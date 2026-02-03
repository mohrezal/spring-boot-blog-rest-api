package com.github.mohrezal.api.domains.users.commands;

import com.github.mohrezal.api.domains.users.commands.params.RefreshTokenCommandParams;
import com.github.mohrezal.api.domains.users.dtos.AuthResponse;
import com.github.mohrezal.api.domains.users.exceptions.types.UserInvalidRefreshTokenException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserRefreshTokenNotFoundException;
import com.github.mohrezal.api.domains.users.models.RefreshToken;
import com.github.mohrezal.api.domains.users.models.User;
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AuthResponse execute(RefreshTokenCommandParams params) {
        if (params.getRefreshToken() == null
                || !jwtService.validateRefreshToken(params.getRefreshToken())) {
            throw new UserInvalidRefreshTokenException();
        }

        RefreshToken refreshTokenEntity =
                jwtService
                        .getRefreshTokenEntity(params.getRefreshToken())
                        .orElseThrow(UserRefreshTokenNotFoundException::new);

        User user =
                userRepository
                        .findById(refreshTokenEntity.getUser().getId())
                        .orElseThrow(UserNotFoundException::new);

        jwtService.revokeRefreshToken(params.getRefreshToken());

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user.getId());

        String deviceName = deviceInfoService.parseDeviceName(params.getUserAgent());

        jwtService.saveRefreshToken(
                newRefreshToken, user, params.getIpAddress(), params.getUserAgent(), deviceName);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}

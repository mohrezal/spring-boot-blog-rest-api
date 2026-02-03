package com.github.mohrezal.api.domains.users.commands;

import com.github.mohrezal.api.domains.users.commands.params.LogoutUserCommandParams;
import com.github.mohrezal.api.domains.users.exceptions.types.UserInvalidRefreshTokenException;
import com.github.mohrezal.api.domains.users.models.RefreshToken;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedCommand;
import com.github.mohrezal.api.shared.exceptions.types.ForbiddenException;
import com.github.mohrezal.api.shared.services.jwt.JwtService;
import java.util.UUID;
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
public class LogoutUserCommand extends AuthenticatedCommand<LogoutUserCommandParams, Void> {

    private final JwtService jwtService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void execute(LogoutUserCommandParams params) {
        if (params.refreshToken() == null
                || !jwtService.validateRefreshToken(params.refreshToken())) {
            throw new UserInvalidRefreshTokenException();
        }

        validate(params);

        UUID currentUserId = user.getId();

        RefreshToken refreshToken =
                jwtService
                        .getRefreshTokenEntity(params.refreshToken())
                        .orElseThrow(UserInvalidRefreshTokenException::new);

        if (!currentUserId.equals(refreshToken.getUser().getId())) {
            throw new ForbiddenException();
        }

        jwtService.revokeRefreshToken(params.refreshToken());

        return null;
    }
}

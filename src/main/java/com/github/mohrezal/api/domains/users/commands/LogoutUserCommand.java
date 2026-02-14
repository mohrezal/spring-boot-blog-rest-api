package com.github.mohrezal.api.domains.users.commands;

import com.github.mohrezal.api.domains.users.commands.params.LogoutUserCommandParams;
import com.github.mohrezal.api.domains.users.exceptions.context.UserLogoutExceptionContext;
import com.github.mohrezal.api.domains.users.exceptions.types.UserInvalidRefreshTokenException;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedCommand;
import com.github.mohrezal.api.shared.exceptions.types.ForbiddenException;
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
public class LogoutUserCommand extends AuthenticatedCommand<LogoutUserCommandParams, Void> {

    private final JwtService jwtService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void execute(LogoutUserCommandParams params) {

        validate(params);
        var context =
                new UserLogoutExceptionContext(
                        user.getId().toString(),
                        params.refreshToken() != null && !params.refreshToken().isBlank());

        if (!context.hasRefreshToken()) {
            throw new UserInvalidRefreshTokenException(context);
        }

        var refreshToken =
                jwtService
                        .getRefreshTokenEntity(params.refreshToken())
                        .orElseThrow(() -> new UserInvalidRefreshTokenException(context));

        if (!user.getId().equals(refreshToken.getUser().getId())) {
            throw new ForbiddenException(context);
        }

        jwtService.revokeRefreshToken(params.refreshToken());
        log.info("Logout successful for user session");

        return null;
    }
}

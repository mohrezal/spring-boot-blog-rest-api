package com.github.mohrezal.api.domains.users.commands;

import com.github.mohrezal.api.domains.users.commands.params.LogoutUserCommandParams;
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
        try {
            if (params.refreshToken() == null || params.refreshToken().isBlank()) {
                throw new UserInvalidRefreshTokenException();
            }
            var refreshToken =
                    jwtService
                            .getRefreshTokenEntity(params.refreshToken())
                            .orElseThrow(UserInvalidRefreshTokenException::new);

            if (!user.getId().equals(refreshToken.getUser().getId())) {
                log.warn("Logout forbidden - token ownership mismatch for user session");
                throw new ForbiddenException();
            }

            jwtService.revokeRefreshToken(params.refreshToken());

            log.info("Logout successful for user session");

            return null;

        } catch (UserInvalidRefreshTokenException | ForbiddenException ex) {
            log.warn("Logout failed - authentication error: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error during logout operation", ex);
            throw ex;
        }
    }
}

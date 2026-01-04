package com.github.mohrezal.springbootblogrestapi.domains.users.commands;

import com.github.mohrezal.springbootblogrestapi.domains.users.commands.params.LogoutUserCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types.UserInvalidRefreshTokenException;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.RefreshToken;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.ForbiddenException;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Command;
import com.github.mohrezal.springbootblogrestapi.shared.services.jwt.JwtService;
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
public class LogoutUserCommand implements Command<LogoutUserCommandParams, Void> {

    private final JwtService jwtService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void execute(LogoutUserCommandParams params) {
        if (params.getRefreshToken() == null
                || !jwtService.validateRefreshToken(params.getRefreshToken())) {
            throw new UserInvalidRefreshTokenException();
        }

        if (!(params.getUserDetails() instanceof User currentUser)) {
            throw new ForbiddenException();
        }

        UUID currentUserId = currentUser.getId();

        RefreshToken refreshToken =
                jwtService
                        .getRefreshTokenEntity(params.getRefreshToken())
                        .orElseThrow(UserInvalidRefreshTokenException::new);

        if (!currentUserId.equals(refreshToken.getUser().getId())) {
            throw new ForbiddenException();
        }

        jwtService.revokeRefreshToken(params.getRefreshToken());

        return null;
    }
}

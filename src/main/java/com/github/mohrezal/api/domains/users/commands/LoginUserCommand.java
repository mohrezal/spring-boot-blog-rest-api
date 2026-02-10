package com.github.mohrezal.api.domains.users.commands;

import com.github.mohrezal.api.domains.users.commands.params.LoginUserCommandParams;
import com.github.mohrezal.api.domains.users.dtos.AuthResponse;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.services.authentication.AuthenticationService;
import com.github.mohrezal.api.shared.interfaces.Command;
import com.github.mohrezal.api.shared.services.deviceinfo.RequestInfoService;
import com.github.mohrezal.api.shared.services.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Slf4j
public class LoginUserCommand implements Command<LoginUserCommandParams, AuthResponse> {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final RequestInfoService deviceInfoService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AuthResponse execute(LoginUserCommandParams params) {
        try {
            var user = authenticationService.authenticate(params.loginRequest());

            var accessToken = jwtService.generateAccessToken(user);
            var refreshToken = jwtService.generateRefreshToken(user.getId());
            var deviceName = deviceInfoService.parseDeviceName(params.userAgent());

            jwtService.saveRefreshToken(
                    refreshToken, user, params.ipAddress(), params.userAgent(), deviceName);
            log.info("The user with id={} successfully logged in.", user.getId());

            return new AuthResponse(accessToken, refreshToken);
        } catch (BadCredentialsException | UserNotFoundException e) {
            log.warn("Login failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in LoginUserCommand - params : {}", params);
            throw e;
        }
    }
}

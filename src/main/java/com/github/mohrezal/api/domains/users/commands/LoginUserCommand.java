package com.github.mohrezal.api.domains.users.commands;

import com.github.mohrezal.api.domains.posts.repositories.PostViewRepository;
import com.github.mohrezal.api.domains.users.commands.params.LoginUserCommandParams;
import com.github.mohrezal.api.domains.users.dtos.AuthResponse;
import com.github.mohrezal.api.domains.users.exceptions.context.UserLoginExceptionContext;
import com.github.mohrezal.api.domains.users.exceptions.types.UserInvalidCredentialsException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.services.authentication.AuthenticationService;
import com.github.mohrezal.api.shared.interfaces.Command;
import com.github.mohrezal.api.shared.services.deviceinfo.RequestInfoService;
import com.github.mohrezal.api.shared.services.hash.HashService;
import com.github.mohrezal.api.shared.services.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginUserCommand implements Command<LoginUserCommandParams, AuthResponse> {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final RequestInfoService deviceInfoService;
    private final PostViewRepository postViewRepository;
    private final HashService hashService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AuthResponse execute(LoginUserCommandParams params) {
        String deviceName = null;

        try {
            var user = authenticationService.authenticate(params.loginRequest());
            deviceName = deviceInfoService.parseDeviceName(params.userAgent());

            var accessToken = jwtService.generateAccessToken(user);
            var refreshToken = jwtService.generateRefreshToken(user.getId());

            jwtService.saveRefreshToken(
                    refreshToken, user, params.ipAddress(), params.userAgent(), deviceName);

            if (params.vid() != null && !params.vid().isBlank()) {
                try {
                    var vidHashed = hashService.sha256(params.vid());
                    postViewRepository.linkAnonymousViewsToUserByVidHash(vidHashed, user.getId());
                } catch (RuntimeException ex) {
                    log.error("Failed to link anonymous views for user id={}.", user.getId(), ex);
                }
            }

            log.info("The user with id={} successfully logged in.", user.getId());

            return new AuthResponse(accessToken, refreshToken);
        } catch (BadCredentialsException | UserNotFoundException e) {
            var context = new UserLoginExceptionContext(params.loginRequest().email(), deviceName);
            throw new UserInvalidCredentialsException(context, e);
        }
    }
}

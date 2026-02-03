package com.github.mohrezal.api.domains.users.controllers;

import com.github.mohrezal.api.config.Routes;
import com.github.mohrezal.api.domains.users.commands.LoginUserCommand;
import com.github.mohrezal.api.domains.users.commands.LogoutUserCommand;
import com.github.mohrezal.api.domains.users.commands.RefreshTokenCommand;
import com.github.mohrezal.api.domains.users.commands.RegisterUserCommand;
import com.github.mohrezal.api.domains.users.commands.params.LoginUserCommandParams;
import com.github.mohrezal.api.domains.users.commands.params.LogoutUserCommandParams;
import com.github.mohrezal.api.domains.users.commands.params.RefreshTokenCommandParams;
import com.github.mohrezal.api.domains.users.commands.params.RegisterUserCommandParams;
import com.github.mohrezal.api.domains.users.dtos.AuthResponse;
import com.github.mohrezal.api.domains.users.dtos.LoginRequest;
import com.github.mohrezal.api.domains.users.dtos.RegisterResponse;
import com.github.mohrezal.api.domains.users.dtos.RegisterUserRequest;
import com.github.mohrezal.api.domains.users.dtos.UserSummary;
import com.github.mohrezal.api.shared.annotations.IsAdminOrUser;
import com.github.mohrezal.api.shared.constants.CookieConstants;
import com.github.mohrezal.api.shared.utils.CookieUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.Auth.BASE)
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {

    private final ObjectProvider<@NonNull RegisterUserCommand> registerUserCommandProvider;
    private final ObjectProvider<@NonNull LoginUserCommand> loginUserCommandProvider;
    private final ObjectProvider<@NonNull RefreshTokenCommand> refreshTokenCommandProvider;
    private final ObjectProvider<@NonNull LogoutUserCommand> logoutUserCommandProvider;
    private final CookieUtils cookieUtils;

    @PostMapping(Routes.Auth.REGISTER)
    public ResponseEntity<@NonNull UserSummary> register(
            @Valid @RequestBody RegisterUserRequest registerUser, HttpServletRequest request) {
        RegisterUserCommandParams params =
                RegisterUserCommandParams.builder()
                        .registerUserRequest(registerUser)
                        .ipAddress(request.getRemoteAddr())
                        .userAgent(request.getHeader(HttpHeaders.USER_AGENT))
                        .build();

        var command = registerUserCommandProvider.getObject();
        command.validate(params);
        RegisterResponse registerResponse = command.execute(params);

        ResponseCookie accessTokenCookie =
                cookieUtils.createAccessTokenCookie(
                        registerResponse.getAuthResponse().getAccessToken());
        ResponseCookie refreshTokenCookie =
                cookieUtils.createRefreshTokenCookie(
                        registerResponse.getAuthResponse().getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(registerResponse.getUser());
    }

    @PostMapping(Routes.Auth.LOGIN)
    public ResponseEntity<Void> login(
            @Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        LoginUserCommandParams params =
                LoginUserCommandParams.builder()
                        .loginRequest(loginRequest)
                        .ipAddress(request.getRemoteAddr())
                        .userAgent(request.getHeader(HttpHeaders.USER_AGENT))
                        .build();

        AuthResponse authResponse = loginUserCommandProvider.getObject().execute(params);

        ResponseCookie accessTokenCookie =
                cookieUtils.createAccessTokenCookie(authResponse.getAccessToken());
        ResponseCookie refreshTokenCookie =
                cookieUtils.createRefreshTokenCookie(authResponse.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }

    @PostMapping(Routes.Auth.REFRESH)
    public ResponseEntity<Void> refresh(HttpServletRequest request) {
        String refreshToken =
                cookieUtils.getCookieValue(
                        request.getCookies(), CookieConstants.REFRESH_TOKEN_COOKIE_NAME);

        RefreshTokenCommandParams params =
                RefreshTokenCommandParams.builder()
                        .refreshToken(refreshToken)
                        .ipAddress(request.getRemoteAddr())
                        .userAgent(request.getHeader(HttpHeaders.USER_AGENT))
                        .build();

        AuthResponse authResponse = refreshTokenCommandProvider.getObject().execute(params);

        ResponseCookie accessTokenCookie =
                cookieUtils.createAccessTokenCookie(authResponse.getAccessToken());
        ResponseCookie refreshTokenCookie =
                cookieUtils.createRefreshTokenCookie(authResponse.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }

    @IsAdminOrUser
    @PostMapping(Routes.Auth.LOGOUT)
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
        String refreshToken =
                cookieUtils.getCookieValue(
                        request.getCookies(), CookieConstants.REFRESH_TOKEN_COOKIE_NAME);

        LogoutUserCommandParams params =
                LogoutUserCommandParams.builder()
                        .userDetails(userDetails)
                        .refreshToken(refreshToken)
                        .build();

        logoutUserCommandProvider.getObject().execute(params);

        ResponseCookie accessTokenCookie = cookieUtils.deleteAccessTokenCookie();
        ResponseCookie refreshTokenCookie = cookieUtils.deleteRefreshTokenCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }
}

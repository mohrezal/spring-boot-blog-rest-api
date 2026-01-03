package com.github.mohrezal.springbootblogrestapi.domains.users.controllers;

import com.github.mohrezal.springbootblogrestapi.config.Routes;
import com.github.mohrezal.springbootblogrestapi.domains.users.commands.LoginUserCommand;
import com.github.mohrezal.springbootblogrestapi.domains.users.commands.RefreshTokenCommand;
import com.github.mohrezal.springbootblogrestapi.domains.users.commands.RegisterUserCommand;
import com.github.mohrezal.springbootblogrestapi.domains.users.commands.params.LoginUserCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.users.commands.params.RefreshTokenCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.users.commands.params.RegisterUserCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.AuthResponse;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.LoginRequest;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.RegisterResponse;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.RegisterUserRequest;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.UserSummary;
import com.github.mohrezal.springbootblogrestapi.shared.config.ApplicationProperties;
import com.github.mohrezal.springbootblogrestapi.shared.constants.CookieConstants;
import com.github.mohrezal.springbootblogrestapi.shared.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.Auth.BASE)
@RequiredArgsConstructor
public class AuthController {

    private final ObjectProvider<@NonNull RegisterUserCommand> registerUserCommandProvider;
    private final ObjectProvider<@NonNull LoginUserCommand> loginUserCommandProvider;
    private final ObjectProvider<@NonNull RefreshTokenCommand> refreshTokenCommandProvider;
    private final CookieUtil cookieUtil;
    private final ApplicationProperties applicationProperties;

    @PostMapping(Routes.Auth.REGISTER)
    public ResponseEntity<@NonNull UserSummary> register(
            @Valid @RequestBody RegisterUserRequest registerUser, HttpServletRequest request) {
        RegisterUserCommandParams params =
                RegisterUserCommandParams.builder()
                        .registerUserRequest(registerUser)
                        .ipAddress(request.getRemoteAddr())
                        .userAgent(request.getHeader(HttpHeaders.USER_AGENT))
                        .build();

        RegisterResponse registerResponse = registerUserCommandProvider.getObject().execute(params);

        ResponseCookie accessTokenCookie =
                createAccessTokenCookie(registerResponse.getAuthResponse().getAccessToken());
        ResponseCookie refreshTokenCookie =
                createRefreshTokenCookie(registerResponse.getAuthResponse().getRefreshToken());

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

        ResponseCookie accessTokenCookie = createAccessTokenCookie(authResponse.getAccessToken());
        ResponseCookie refreshTokenCookie =
                createRefreshTokenCookie(authResponse.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }

    @PostMapping(Routes.Auth.REFRESH)
    public ResponseEntity<Void> refresh(HttpServletRequest request) {
        String refreshToken =
                cookieUtil.getCookieValue(
                        request.getCookies(), CookieConstants.REFRESH_TOKEN_COOKIE_NAME);

        RefreshTokenCommandParams params =
                RefreshTokenCommandParams.builder()
                        .refreshToken(refreshToken)
                        .ipAddress(request.getRemoteAddr())
                        .userAgent(request.getHeader(HttpHeaders.USER_AGENT))
                        .build();

        AuthResponse authResponse = refreshTokenCommandProvider.getObject().execute(params);

        ResponseCookie accessTokenCookie = createAccessTokenCookie(authResponse.getAccessToken());
        ResponseCookie refreshTokenCookie =
                createRefreshTokenCookie(authResponse.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }

    private ResponseCookie createAccessTokenCookie(String accessToken) {
        return cookieUtil.createCookie(
                CookieConstants.ACCESS_TOKEN_COOKIE_NAME,
                accessToken,
                applicationProperties.security().accessTokenLifeTime(),
                "/");
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return cookieUtil.createCookie(
                CookieConstants.REFRESH_TOKEN_COOKIE_NAME,
                refreshToken,
                applicationProperties.security().refreshTokenLifeTime(),
                Routes.build(Routes.Auth.BASE, Routes.Auth.REFRESH));
    }
}

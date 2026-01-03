package com.github.mohrezal.springbootblogrestapi.domains.users.controllers;

import com.github.mohrezal.springbootblogrestapi.config.Routes;
import com.github.mohrezal.springbootblogrestapi.domains.users.commands.LoginUserCommand;
import com.github.mohrezal.springbootblogrestapi.domains.users.commands.RegisterUserCommand;
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

    private final RegisterUserCommand registerUserCommand;
    private final LoginUserCommand loginUserCommand;
    private final CookieUtil cookieUtil;
    private final ApplicationProperties applicationProperties;

    @PostMapping(Routes.Auth.REGISTER)
    public ResponseEntity<@NonNull UserSummary> register(
            @Valid @RequestBody RegisterUserRequest registerUser) {
        RegisterResponse registerResponse = registerUserCommand.execute(registerUser);

        ResponseCookie accessTokenCookie =
                cookieUtil.createCookie(
                        CookieConstants.ACCESS_TOKEN_COOKIE_NAME,
                        registerResponse.getAuthResponse().getAccessToken(),
                        applicationProperties.security().accessTokenLifeTime(),
                        "/");

        ResponseCookie refreshTokenCookie =
                cookieUtil.createCookie(
                        CookieConstants.REFRESH_TOKEN_COOKIE_NAME,
                        registerResponse.getAuthResponse().getRefreshToken(),
                        applicationProperties.security().refreshTokenLifeTime(),
                        Routes.build(Routes.Auth.BASE, Routes.Auth.REFRESH));

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(registerResponse.getUser());
    }

    @PostMapping(Routes.Auth.LOGIN)
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = loginUserCommand.execute(loginRequest);

        ResponseCookie accessTokenCookie =
                cookieUtil.createCookie(
                        CookieConstants.ACCESS_TOKEN_COOKIE_NAME,
                        authResponse.getAccessToken(),
                        applicationProperties.security().accessTokenLifeTime(),
                        "/");

        ResponseCookie refreshTokenCookie =
                cookieUtil.createCookie(
                        CookieConstants.REFRESH_TOKEN_COOKIE_NAME,
                        authResponse.getRefreshToken(),
                        applicationProperties.security().refreshTokenLifeTime(),
                        Routes.build(Routes.Auth.BASE, Routes.Auth.REFRESH));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }

    @PostMapping(Routes.Auth.REFRESH)
    public ResponseEntity<Void> refresh(HttpServletRequest request) {
        return ResponseEntity.ok().build();
    }
}

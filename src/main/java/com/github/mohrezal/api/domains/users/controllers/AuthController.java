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
import com.github.mohrezal.api.domains.users.dtos.CsrfTokenResponse;
import com.github.mohrezal.api.domains.users.dtos.LoginRequest;
import com.github.mohrezal.api.domains.users.dtos.RegisterUserRequest;
import com.github.mohrezal.api.domains.users.dtos.UserSummary;
import com.github.mohrezal.api.shared.annotations.IsAdminOrUser;
import com.github.mohrezal.api.shared.constants.CookieConstants;
import com.github.mohrezal.api.shared.services.deviceinfo.RequestInfoService;
import com.github.mohrezal.api.shared.utils.CookieUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final RequestInfoService requestInfoService;

    @GetMapping(Routes.Auth.CSRF)
    public ResponseEntity<CsrfTokenResponse> csrf(CsrfToken csrfToken) {
        return ResponseEntity.ok(
                new CsrfTokenResponse(csrfToken.getToken(), csrfToken.getHeaderName()));
    }

    @PostMapping(Routes.Auth.REGISTER)
    public ResponseEntity<@NonNull UserSummary> register(
            @Valid @RequestBody RegisterUserRequest registerUser, HttpServletRequest request) {

        var params =
                new RegisterUserCommandParams(
                        registerUser,
                        requestInfoService.getClientIp(request),
                        request.getHeader(HttpHeaders.USER_AGENT));

        var command = registerUserCommandProvider.getObject();
        var registerResponse = command.execute(params);

        var accessTokenCookie =
                cookieUtils.createAccessTokenCookie(registerResponse.authResponse().accessToken());
        var refreshTokenCookie =
                cookieUtils.createRefreshTokenCookie(
                        registerResponse.authResponse().refreshToken());

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(registerResponse.user());
    }

    @PostMapping(Routes.Auth.LOGIN)
    public ResponseEntity<Void> login(
            @Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {

        var params =
                new LoginUserCommandParams(
                        loginRequest,
                        requestInfoService.getClientIp(request),
                        request.getHeader(HttpHeaders.USER_AGENT));

        var authResponse = loginUserCommandProvider.getObject().execute(params);

        var accessTokenCookie = cookieUtils.createAccessTokenCookie(authResponse.accessToken());
        var refreshTokenCookie = cookieUtils.createRefreshTokenCookie(authResponse.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }

    @PostMapping(Routes.Auth.REFRESH)
    public ResponseEntity<Void> refresh(HttpServletRequest request) {
        var refreshToken =
                cookieUtils.getCookieValue(
                        request.getCookies(), CookieConstants.REFRESH_TOKEN_COOKIE_NAME);

        var params =
                new RefreshTokenCommandParams(
                        refreshToken,
                        requestInfoService.getClientIp(request),
                        request.getHeader(HttpHeaders.USER_AGENT));

        var authResponse = refreshTokenCommandProvider.getObject().execute(params);

        var accessTokenCookie = cookieUtils.createAccessTokenCookie(authResponse.accessToken());
        var refreshTokenCookie = cookieUtils.createRefreshTokenCookie(authResponse.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }

    @IsAdminOrUser
    @PostMapping(Routes.Auth.LOGOUT)
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
        var refreshToken =
                cookieUtils.getCookieValue(
                        request.getCookies(), CookieConstants.REFRESH_TOKEN_COOKIE_NAME);

        var params = new LogoutUserCommandParams(userDetails, refreshToken);

        logoutUserCommandProvider.getObject().execute(params);

        var accessTokenCookie = cookieUtils.deleteAccessTokenCookie();
        var refreshTokenCookie = cookieUtils.deleteRefreshTokenCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }
}

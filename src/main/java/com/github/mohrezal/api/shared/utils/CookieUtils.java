package com.github.mohrezal.api.shared.utils;

import com.github.mohrezal.api.config.Routes;
import com.github.mohrezal.api.shared.config.ApplicationProperties;
import com.github.mohrezal.api.shared.constants.CookieConstants;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieUtils {

    private final ApplicationProperties applicationProperties;

    public String getCookieValue(Cookie[] cookies, String cookieName) {
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public ResponseCookie createCookie(String name, String value, long maxAgeSeconds) {
        return createCookie(name, value, maxAgeSeconds, "/");
    }

    public ResponseCookie createCookie(String name, String value, long maxAgeSeconds, String path) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .path(path)
                .maxAge(maxAgeSeconds)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie deleteCookie(String name) {
        return deleteCookie(name, "/");
    }

    public ResponseCookie deleteCookie(String name, String path) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .path(path)
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie createAccessTokenCookie(String accessToken) {
        return createCookie(
                CookieConstants.ACCESS_TOKEN_COOKIE_NAME,
                accessToken,
                applicationProperties.security().accessTokenLifeTime(),
                "/");
    }

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return createCookie(
                CookieConstants.REFRESH_TOKEN_COOKIE_NAME,
                refreshToken,
                applicationProperties.security().refreshTokenLifeTime(),
                Routes.Auth.BASE);
    }

    public ResponseCookie deleteAccessTokenCookie() {
        return deleteCookie(CookieConstants.ACCESS_TOKEN_COOKIE_NAME, "/");
    }

    public ResponseCookie deleteRefreshTokenCookie() {
        return deleteCookie(CookieConstants.REFRESH_TOKEN_COOKIE_NAME, Routes.Auth.BASE);
    }
}

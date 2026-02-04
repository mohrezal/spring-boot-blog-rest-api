package com.github.mohrezal.api.shared.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.config.Routes;
import com.github.mohrezal.api.shared.config.ApplicationProperties;
import com.github.mohrezal.api.shared.constants.CookieConstants;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;

@ExtendWith(MockitoExtension.class)
class CookieUtilsTest {

    private static final String COOKIE_NAME = "test-cookie";
    private static final String COOKIE_VALUE = "test-value";
    private static final long MAX_AGE = 3600L;

    @Mock private ApplicationProperties applicationProperties;

    @Mock private ApplicationProperties.Security security;

    @InjectMocks private CookieUtils cookieUtils;

    @Test
    void getCookieValue_whenCookiesIsNullOrEmpty_shouldReturnNull() {
        assertNull(cookieUtils.getCookieValue(null, COOKIE_NAME));
        assertNull(cookieUtils.getCookieValue(new Cookie[0], COOKIE_NAME));
    }

    @Test
    void getCookieValue_whenCookieNotFound_shouldReturnNull() {
        Cookie[] cookies = {new Cookie("other-cookie", "other-value")};

        assertNull(cookieUtils.getCookieValue(cookies, COOKIE_NAME));
    }

    @Test
    void getCookieValue_whenCookieFound_shouldReturnValue() {
        Cookie[] cookies = {
            new Cookie("other-cookie", "other-value"), new Cookie(COOKIE_NAME, COOKIE_VALUE)
        };

        assertEquals(COOKIE_VALUE, cookieUtils.getCookieValue(cookies, COOKIE_NAME));
    }

    @Test
    void createCookie_shouldSetCorrectAttributes() {
        ResponseCookie cookie = cookieUtils.createCookie(COOKIE_NAME, COOKIE_VALUE, MAX_AGE);

        assertEquals(COOKIE_NAME, cookie.getName());
        assertEquals(COOKIE_VALUE, cookie.getValue());
        assertEquals(MAX_AGE, cookie.getMaxAge().getSeconds());
        assertEquals("/", cookie.getPath());
        assertTrue(cookie.isHttpOnly());
        assertTrue(cookie.isSecure());
        assertEquals("Strict", cookie.getSameSite());
    }

    @Test
    void createCookie_withCustomPath_shouldSetPath() {
        ResponseCookie cookie =
                cookieUtils.createCookie(COOKIE_NAME, COOKIE_VALUE, MAX_AGE, "/api");

        assertEquals("/api", cookie.getPath());
    }

    @Test
    void deleteCookie_shouldSetMaxAgeToZero() {
        ResponseCookie cookie = cookieUtils.deleteCookie(COOKIE_NAME);

        assertEquals(COOKIE_NAME, cookie.getName());
        assertEquals("", cookie.getValue());
        assertEquals(0, cookie.getMaxAge().getSeconds());
        assertEquals("/", cookie.getPath());
    }

    @Test
    void deleteCookie_withCustomPath_shouldSetPath() {
        ResponseCookie cookie = cookieUtils.deleteCookie(COOKIE_NAME, "/api");

        assertEquals("/api", cookie.getPath());
        assertEquals(0, cookie.getMaxAge().getSeconds());
    }

    @Test
    void createAccessTokenCookie_shouldUseConfiguredLifetime() {
        when(applicationProperties.security()).thenReturn(security);
        when(security.accessTokenLifeTime()).thenReturn(1800L);

        ResponseCookie cookie = cookieUtils.createAccessTokenCookie("access-token");

        assertEquals(CookieConstants.ACCESS_TOKEN_COOKIE_NAME, cookie.getName());
        assertEquals("access-token", cookie.getValue());
        assertEquals(1800L, cookie.getMaxAge().getSeconds());
        assertEquals("/", cookie.getPath());
    }

    @Test
    void createRefreshTokenCookie_shouldUseConfiguredLifetimeAndPath() {
        when(applicationProperties.security()).thenReturn(security);
        when(security.refreshTokenLifeTime()).thenReturn(604800L);

        ResponseCookie cookie = cookieUtils.createRefreshTokenCookie("refresh-token");

        assertEquals(CookieConstants.REFRESH_TOKEN_COOKIE_NAME, cookie.getName());
        assertEquals("refresh-token", cookie.getValue());
        assertEquals(604800L, cookie.getMaxAge().getSeconds());
        assertEquals(Routes.Auth.BASE, cookie.getPath());
    }

    @Test
    void deleteAccessTokenCookie_shouldReturnCorrectCookie() {
        ResponseCookie cookie = cookieUtils.deleteAccessTokenCookie();

        assertEquals(CookieConstants.ACCESS_TOKEN_COOKIE_NAME, cookie.getName());
        assertEquals(0, cookie.getMaxAge().getSeconds());
        assertEquals("/", cookie.getPath());
    }

    @Test
    void deleteRefreshTokenCookie_shouldReturnCorrectCookie() {
        ResponseCookie cookie = cookieUtils.deleteRefreshTokenCookie();

        assertEquals(CookieConstants.REFRESH_TOKEN_COOKIE_NAME, cookie.getName());
        assertEquals(0, cookie.getMaxAge().getSeconds());
        assertEquals(Routes.Auth.BASE, cookie.getPath());
    }
}

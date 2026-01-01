package com.github.mohrezal.springbootblogrestapi.config;

import com.github.mohrezal.springbootblogrestapi.shared.constants.CookieConstants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
public class CookieBearerTokenResolver implements BearerTokenResolver {

    @Override
    public String resolve(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, CookieConstants.ACCESS_TOKEN_COOKIE_NAME);
        return cookie != null ? cookie.getValue() : null;
    }
}

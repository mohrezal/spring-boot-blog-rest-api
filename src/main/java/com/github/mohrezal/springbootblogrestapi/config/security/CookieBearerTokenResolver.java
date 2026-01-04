package com.github.mohrezal.springbootblogrestapi.config.security;

import com.github.mohrezal.springbootblogrestapi.shared.constants.CookieConstants;
import com.github.mohrezal.springbootblogrestapi.shared.constants.SecurityConstants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
public class CookieBearerTokenResolver implements BearerTokenResolver {

    @Override
    public String resolve(HttpServletRequest request) {
        if (Boolean.TRUE.equals(
                request.getAttribute(SecurityConstants.SKIP_JWT_VALIDATION_ATTRIBUTE))) {
            return null;
        }

        Cookie cookie = WebUtils.getCookie(request, CookieConstants.ACCESS_TOKEN_COOKIE_NAME);
        return cookie != null ? cookie.getValue() : null;
    }
}

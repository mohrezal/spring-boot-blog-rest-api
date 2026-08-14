package com.github.mohrezal.api.support.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

import com.github.mohrezal.api.domains.users.models.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public final class AuthenticationUtils {
    private AuthenticationUtils() {}

    public static RequestPostProcessor authenticate(User user) {
        return authentication(
                new UsernamePasswordAuthenticationToken(user, "mock-token", user.getAuthorities()));
    }
}

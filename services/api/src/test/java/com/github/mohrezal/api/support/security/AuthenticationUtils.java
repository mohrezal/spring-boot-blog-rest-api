package com.github.mohrezal.api.support.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

import com.github.mohrezal.api.domains.users.models.User;
import java.util.Arrays;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public final class AuthenticationUtils {
    private AuthenticationUtils() {}

    public static RequestPostProcessor authenticate(User user, String... permissions) {
        var authorities = Arrays.stream(permissions).map(SimpleGrantedAuthority::new).toList();
        return authentication(
                new UsernamePasswordAuthenticationToken(user, "mock-token", authorities));
    }

    public static RequestPostProcessor authenticate(User user) {
        return authenticate(user, new String[0]);
    }
}

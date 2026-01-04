package com.github.mohrezal.springbootblogrestapi.config;

import com.github.mohrezal.springbootblogrestapi.shared.constants.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class SkipJwtValidationFilter extends OncePerRequestFilter {

    private static final String REFRESH_ENDPOINT =
            Routes.build(Routes.Auth.BASE, Routes.Auth.REFRESH);
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (HttpMethod.POST.matches(request.getMethod())
                && pathMatcher.match(REFRESH_ENDPOINT, request.getRequestURI())) {
            request.setAttribute(SecurityConstants.SKIP_JWT_VALIDATION_ATTRIBUTE, true);
        }

        filterChain.doFilter(request, response);
    }
}

package com.github.mohrezal.api.config.security;

import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.services.jwt.JwtService;
import com.github.mohrezal.common.constants.CookieConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null
                && request.getCookies() != null) {
            var accessToken =
                    Arrays.stream(request.getCookies())
                            .filter(
                                    cookie ->
                                            CookieConstants.ACCESS_TOKEN_COOKIE_NAME.equals(
                                                    cookie.getName()))
                            .map(Cookie::getValue)
                            .filter(StringUtils::hasText)
                            .findFirst();

            if (accessToken.isEmpty()) {
                log.debug("No access token cookie found for {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            if (!jwtTokenProvider.isAccessToken(accessToken.get())) {
                log.warn("Rejected non-access token for {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            if (jwtService.isAccessTokenRevoked(accessToken.get())) {
                log.warn("Rejected revoked access token for {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            var userId = jwtTokenProvider.extractUserId(accessToken.get());
            if (userId.isEmpty()) {
                log.warn("Failed to parse access token for {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            var user = userRepository.findById(userId.get());
            if (user.isEmpty()) {
                log.warn("User not found for token userId={}", userId.get());
                filterChain.doFilter(request, response);
                return;
            }

            var validUser = user.get();
            if (!(validUser.isEnabled()
                    && validUser.isAccountNonExpired()
                    && validUser.isAccountNonLocked()
                    && validUser.isCredentialsNonExpired()
                    && validUser.hasVerifiedEmail())) {
                log.warn("User account not active userId={}", userId.get());
                filterChain.doFilter(request, response);
                return;
            }

            var tokenPrivilegeVersion = jwtTokenProvider.extractPrivilegeVersion(accessToken.get());
            if (tokenPrivilegeVersion != validUser.getPrivilegeVersion()) {
                log.warn(
                        "Access token privilege version mismatch userId={}, tokenVersion={},"
                                + " currentVersion={}",
                        userId.get(),
                        tokenPrivilegeVersion,
                        validUser.getPrivilegeVersion());
                filterChain.doFilter(request, response);
                return;
            }

            var authorities =
                    jwtTokenProvider.extractPermissionKeys(accessToken.get()).stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();
            var authentication =
                    UsernamePasswordAuthenticationToken.authenticated(validUser, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            var context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            MDC.put("userId", validUser.getId().toString());

            log.debug("Authenticated user userId={}", userId.get());
        }

        filterChain.doFilter(request, response);
    }
}

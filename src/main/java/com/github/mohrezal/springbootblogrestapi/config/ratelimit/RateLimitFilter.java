package com.github.mohrezal.springbootblogrestapi.config.ratelimit;

import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.shared.services.ratelimit.RateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        RateLimitConfig.RateLimitPolicy policy = RateLimitConfig.fromPath(method, path);

        if (policy.ipLimit() != null) {
            String ipKey = "ip:" + request.getRemoteAddr();
            RateLimitService.ConsumptionResult result =
                    rateLimitService.tryConsume(ipKey, policy.ipLimit());
            if (!result.allowed()) {
                sendRateLimitResponse(response, result);
                return;
            }
        }

        if (policy.userLimit() != null) {
            String userId = getAuthenticatedUserId();
            if (userId != null) {
                String userKey = "user:" + userId;
                RateLimitService.ConsumptionResult result =
                        rateLimitService.tryConsume(userKey, policy.userLimit());
                if (!result.allowed()) {
                    sendRateLimitResponse(response, result);
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendRateLimitResponse(
            HttpServletResponse response, RateLimitService.ConsumptionResult result) {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(result.retryAfterSeconds()));
    }

    private String getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User user) {
            return user.getId().toString();
        }
        return null;
    }
}

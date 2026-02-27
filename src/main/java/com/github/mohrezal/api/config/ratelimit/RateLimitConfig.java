package com.github.mohrezal.api.config.ratelimit;

import com.github.mohrezal.api.shared.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

@Component
@RequiredArgsConstructor
public class RateLimitConfig {

    public record RateLimitPolicy(
            HttpMethod method, String path, String key, Integer ipLimit, Integer userLimit) {}

    private final ApplicationProperties applicationProperties;
    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    public RateLimitPolicy fromPath(HttpMethod method, String path) {
        var policies = applicationProperties.rateLimit().policies();
        for (ApplicationProperties.RateLimit.Policy policy : policies) {
            boolean methodMatches = policy.method().equals(method);
            boolean pathMatches = MATCHER.match(policy.path(), path);

            if (methodMatches && pathMatches) {
                return new RateLimitPolicy(
                        policy.method(),
                        policy.path(),
                        policy.key(),
                        policy.ipLimit(),
                        policy.userLimit());
            }
        }

        return new RateLimitPolicy(
                null,
                "/**",
                applicationProperties.rateLimit().defaultKey(),
                applicationProperties.rateLimit().defaultIpLimit(),
                applicationProperties.rateLimit().defaultUserLimit());
    }
}

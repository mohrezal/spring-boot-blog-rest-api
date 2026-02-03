package com.github.mohrezal.api.config.ratelimit;

import com.github.mohrezal.api.config.Routes;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;

public final class RateLimitConfig {
    public record RateLimitPolicy(
            HttpMethod method, String path, Integer ipLimit, Integer userLimit) {}

    private RateLimitConfig() {}

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private static final List<RateLimitPolicy> POLICIES =
            List.of(
                    // Auth endpoints start
                    new RateLimitPolicy(
                            HttpMethod.POST,
                            Routes.build(Routes.Auth.BASE, Routes.Auth.LOGIN),
                            10,
                            null),
                    new RateLimitPolicy(
                            HttpMethod.POST,
                            Routes.build(Routes.Auth.BASE, Routes.Auth.REGISTER),
                            5,
                            null),
                    new RateLimitPolicy(
                            HttpMethod.POST,
                            Routes.build(Routes.Auth.BASE, Routes.Auth.REFRESH),
                            20,
                            null),
                    new RateLimitPolicy(
                            HttpMethod.POST,
                            Routes.build(Routes.Auth.BASE, Routes.Auth.LOGOUT),
                            10,
                            null),
                    // Auth endpoints end
                    // Storage endpoints start
                    new RateLimitPolicy(HttpMethod.POST, Routes.Storage.BASE, 20, 10),
                    new RateLimitPolicy(
                            HttpMethod.POST,
                            Routes.build(Routes.Storage.BASE, Routes.Storage.PROFILE),
                            10,
                            5),
                    new RateLimitPolicy(
                            HttpMethod.DELETE, Routes.build(Routes.Storage.BASE, "*"), 20, 10),
                    // Storage endpoints end

                    // Post endpoints start
                    new RateLimitPolicy(HttpMethod.POST, Routes.Post.BASE, 30, 10),
                    new RateLimitPolicy(
                            HttpMethod.PUT, Routes.build(Routes.Post.BASE, "*"), 30, 15),
                    new RateLimitPolicy(
                            HttpMethod.POST,
                            Routes.build(
                                    Routes.Post.BASE,
                                    Routes.Post.PUBLISH_POST.replace("{slug}", "*")),
                            30,
                            15),
                    new RateLimitPolicy(
                            HttpMethod.POST,
                            Routes.build(
                                    Routes.Post.BASE,
                                    Routes.Post.ARCHIVE_POST.replace("{slug}", "*")),
                            30,
                            15),
                    new RateLimitPolicy(
                            HttpMethod.POST,
                            Routes.build(
                                    Routes.Post.BASE,
                                    Routes.Post.UNARCHIVE_POST.replace("{slug}", "*")),
                            30,
                            15),
                    new RateLimitPolicy(
                            HttpMethod.DELETE, Routes.build(Routes.Post.BASE, "*"), 20, 10),
                    // Post endpoints end
                    // User follow endpoints start
                    new RateLimitPolicy(
                            HttpMethod.POST,
                            Routes.build(
                                    Routes.User.BASE,
                                    Routes.User.FOLLOW_USER.replace("{handle}", "*")),
                            60,
                            30),
                    new RateLimitPolicy(
                            HttpMethod.POST,
                            Routes.build(
                                    Routes.User.BASE,
                                    Routes.User.UNFOLLOW_USER.replace("{handle}", "*")),
                            60,
                            30)
                    // User follow endpoints end
                    );
    private static final RateLimitPolicy DEFAULT = new RateLimitPolicy(null, "/**", 100, 50);

    public static RateLimitPolicy fromPath(HttpMethod method, String path) {
        for (RateLimitPolicy policy : POLICIES) {
            boolean methodMatches = policy.method() == null || policy.method().equals(method);
            boolean pathMatches = MATCHER.match(policy.path(), path);

            if (methodMatches && pathMatches) {
                return policy;
            }
        }
        return DEFAULT;
    }
}

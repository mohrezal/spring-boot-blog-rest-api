package com.github.mohrezal.api.shared.utils;

import com.github.mohrezal.api.shared.config.ApplicationProperties;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public final class RedirectUrlUtils {

    private final ApplicationProperties applicationProperties;

    public boolean isValid(String redirectUrl) {
        if (redirectUrl == null || redirectUrl.isBlank()) {
            return false;
        }
        try {
            var uri = URI.create(redirectUrl);
            var host = uri.getHost();

            if (host == null) return false;

            return applicationProperties.security().allowedOrigin().stream()
                    .map(
                            allowed -> {
                                try {
                                    return URI.create(allowed).getHost();
                                } catch (IllegalArgumentException e) {
                                    log.warn("Invalid allowed origin '{}'", allowed, e);
                                    return null;
                                }
                            })
                    .anyMatch(allowedHost -> allowedHost != null && allowedHost.equals(host));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid redirect URL '{}'", redirectUrl, e);
            return false;
        }
    }
}

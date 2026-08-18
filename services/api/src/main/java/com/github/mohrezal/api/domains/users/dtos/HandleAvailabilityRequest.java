package com.github.mohrezal.api.domains.users.dtos;

import java.util.Locale;

public record HandleAvailabilityRequest(String handle) {
    public static final int MIN_LENGTH = 5;
    public static final int MAX_LENGTH = 30;

    public HandleAvailabilityRequest {
        if (handle != null) {
            handle = handle.toLowerCase(Locale.ROOT);
        }
    }
}

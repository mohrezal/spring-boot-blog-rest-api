package com.github.mohrezal.api.domains.users.dtos;

import com.github.mohrezal.common.email.EmailNormalizer;

public record EmailAvailabilityRequest(String email) {
    public static final int MAX_LENGTH = 100;

    public EmailAvailabilityRequest {
        email = EmailNormalizer.normalize(email);
    }
}

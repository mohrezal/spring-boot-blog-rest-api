package com.github.mohrezal.api.domains.users.dtos;

import com.github.mohrezal.api.domains.storage.dtos.StorageSummary;
import com.github.mohrezal.api.domains.users.enums.UserRole;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UserSummary(
        UUID id,
        String email,
        String handle,
        String firstName,
        String lastName,
        String bio,
        StorageSummary avatar,
        UserRole role,
        Boolean isVerified,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {}

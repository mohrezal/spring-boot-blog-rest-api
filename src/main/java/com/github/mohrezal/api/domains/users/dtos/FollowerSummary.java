package com.github.mohrezal.api.domains.users.dtos;

import com.github.mohrezal.api.domains.storage.dtos.StorageSummary;
import java.time.OffsetDateTime;
import java.util.UUID;

public record FollowerSummary(
        UUID id,
        String handle,
        String firstName,
        String lastName,
        StorageSummary avatar,
        boolean isFollowing,
        OffsetDateTime followedAt) {}

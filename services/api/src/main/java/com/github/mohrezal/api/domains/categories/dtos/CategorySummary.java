package com.github.mohrezal.api.domains.categories.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CategorySummary(
        UUID id,
        String name,
        String slug,
        String description,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {}

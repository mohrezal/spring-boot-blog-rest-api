package com.github.mohrezal.api.domains.categories.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CategoryDetail(
        UUID id,
        String name,
        String slug,
        String description,
        boolean hasChildren,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {}

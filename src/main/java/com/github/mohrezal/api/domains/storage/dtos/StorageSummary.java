package com.github.mohrezal.api.domains.storage.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

public record StorageSummary(
        UUID id,
        String filename,
        String originalFilename,
        String mimeType,
        Long size,
        String title,
        String alt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {}

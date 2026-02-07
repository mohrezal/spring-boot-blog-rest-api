package com.github.mohrezal.api.domains.posts.dtos;

import com.github.mohrezal.api.domains.categories.dtos.CategorySummary;
import com.github.mohrezal.api.domains.posts.enums.PostStatus;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record PostSummary(
        UUID id,
        String title,
        String slug,
        String description,
        String avatarUrl,
        Set<CategorySummary> categories,
        PostStatus status,
        AuthorSummary author,
        OffsetDateTime publishedAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {}

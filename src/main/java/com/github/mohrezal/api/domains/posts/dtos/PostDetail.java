package com.github.mohrezal.api.domains.posts.dtos;

import com.github.mohrezal.api.domains.categories.dtos.CategorySummary;
import com.github.mohrezal.api.domains.posts.enums.PostStatus;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record PostDetail(
        UUID id,
        String title,
        String slug,
        PostStatus status,
        String description,
        String content,
        String avatarUrl,
        AuthorSummary author,
        Set<CategorySummary> categories,
        OffsetDateTime publishedAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {}

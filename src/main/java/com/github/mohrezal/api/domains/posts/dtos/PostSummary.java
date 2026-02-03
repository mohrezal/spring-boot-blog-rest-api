package com.github.mohrezal.api.domains.posts.dtos;

import com.github.mohrezal.api.domains.categories.dtos.CategorySummary;
import com.github.mohrezal.api.domains.posts.enums.PostStatus;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostSummary {
    private UUID id;
    private String title;
    private String slug;
    private String description;
    private String avatarUrl;
    private Set<CategorySummary> categories;
    private PostStatus status;
    private AuthorSummary author;
    private OffsetDateTime publishedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

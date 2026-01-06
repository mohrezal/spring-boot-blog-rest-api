package com.github.mohrezal.springbootblogrestapi.domains.posts.dtos;

import com.github.mohrezal.springbootblogrestapi.domains.categories.dtos.CategorySummary;
import com.github.mohrezal.springbootblogrestapi.domains.posts.enums.PostStatus;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostDetail {
    private UUID id;
    private String title;
    private String slug;
    private PostStatus status;
    private String description;
    private String content;
    private String avatarUrl;
    private AuthorSummary author;
    private Set<CategorySummary> categories;
    private OffsetDateTime publishedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

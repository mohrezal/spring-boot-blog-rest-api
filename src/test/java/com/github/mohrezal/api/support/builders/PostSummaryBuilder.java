package com.github.mohrezal.api.support.builders;

import com.github.mohrezal.api.domains.categories.dtos.CategorySummary;
import com.github.mohrezal.api.domains.posts.dtos.AuthorSummary;
import com.github.mohrezal.api.domains.posts.dtos.PostSummary;
import com.github.mohrezal.api.domains.posts.enums.PostStatus;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public final class PostSummaryBuilder {

    private UUID id = UUID.randomUUID();
    private String title = "Test Post";
    private String slug = "test-post";
    private String description = "Test description";
    private String avatarUrl = "https://example.com/avatar.png";
    private Set<CategorySummary> categories = Set.of();
    private PostStatus status = PostStatus.DRAFT;
    private AuthorSummary author =
            new AuthorSummary(UUID.randomUUID(), "test-user", "Test", "User", null);
    private OffsetDateTime publishedAt = OffsetDateTime.now();
    private OffsetDateTime createdAt = OffsetDateTime.now();
    private OffsetDateTime updatedAt = OffsetDateTime.now();
    private Long viewCount = 0L;

    private PostSummaryBuilder() {}

    public static PostSummaryBuilder aPostSummary() {
        return new PostSummaryBuilder();
    }

    public PostSummaryBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public PostSummaryBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public PostSummaryBuilder withViewCount(Long viewCount) {
        this.viewCount = viewCount;
        return this;
    }

    public PostSummaryBuilder withSlug(String slug) {
        this.slug = slug;
        return this;
    }

    public PostSummaryBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public PostSummaryBuilder withAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public PostSummaryBuilder withCategories(Set<CategorySummary> categories) {
        this.categories = categories;
        return this;
    }

    public PostSummaryBuilder withStatus(PostStatus status) {
        this.status = status;
        return this;
    }

    public PostSummaryBuilder withAuthor(AuthorSummary author) {
        this.author = author;
        return this;
    }

    public PostSummaryBuilder withPublishedAt(OffsetDateTime publishedAt) {
        this.publishedAt = publishedAt;
        return this;
    }

    public PostSummaryBuilder withCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public PostSummaryBuilder withUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public PostSummary build() {
        return new PostSummary(
                id,
                title,
                slug,
                description,
                avatarUrl,
                categories,
                status,
                author,
                publishedAt,
                viewCount,
                createdAt,
                updatedAt);
    }
}

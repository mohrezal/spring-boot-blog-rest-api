package com.github.mohrezal.api.support.builders;

import com.github.mohrezal.api.domains.categories.dtos.CategorySummary;
import com.github.mohrezal.api.domains.posts.dtos.AuthorSummary;
import com.github.mohrezal.api.domains.posts.dtos.PostDetail;
import com.github.mohrezal.api.domains.posts.enums.PostStatus;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public final class PostDetailBuilder {

    private UUID id = UUID.randomUUID();
    private String title = "Test Post";
    private String slug = "test-post";
    private PostStatus status = PostStatus.DRAFT;
    private String description = "Test description";
    private String content = "Test content";
    private String avatarUrl = "https://example.com/avatar.png";
    private AuthorSummary author =
            new AuthorSummary(UUID.randomUUID(), "test-user", "Test", "User", null);
    private Set<CategorySummary> categories = Set.of();
    private OffsetDateTime publishedAt = OffsetDateTime.now();
    private OffsetDateTime createdAt = OffsetDateTime.now();
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    private PostDetailBuilder() {}

    public static PostDetailBuilder aPostDetail() {
        return new PostDetailBuilder();
    }

    public PostDetailBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public PostDetailBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public PostDetailBuilder withSlug(String slug) {
        this.slug = slug;
        return this;
    }

    public PostDetailBuilder withStatus(PostStatus status) {
        this.status = status;
        return this;
    }

    public PostDetailBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public PostDetailBuilder withContent(String content) {
        this.content = content;
        return this;
    }

    public PostDetailBuilder withAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public PostDetailBuilder withAuthor(AuthorSummary author) {
        this.author = author;
        return this;
    }

    public PostDetailBuilder withCategories(Set<CategorySummary> categories) {
        this.categories = categories;
        return this;
    }

    public PostDetailBuilder withPublishedAt(OffsetDateTime publishedAt) {
        this.publishedAt = publishedAt;
        return this;
    }

    public PostDetailBuilder withCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public PostDetailBuilder withUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public PostDetail build() {
        return new PostDetail(
                id,
                title,
                slug,
                status,
                description,
                content,
                avatarUrl,
                author,
                categories,
                publishedAt,
                createdAt,
                updatedAt);
    }
}

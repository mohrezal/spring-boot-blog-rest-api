package com.github.mohrezal.api.support.builders;

import com.github.mohrezal.api.domains.categories.models.Category;
import com.github.mohrezal.api.domains.posts.enums.PostLanguage;
import com.github.mohrezal.api.domains.posts.enums.PostStatus;
import com.github.mohrezal.api.domains.posts.models.Post;
import com.github.mohrezal.api.domains.users.models.User;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PostBuilder {

    private UUID id;

    private String title = "Test Post";
    private String slug = "test-post";
    private PostStatus status = PostStatus.DRAFT;
    private String description = "Test description";
    private String content = "Test content";
    private String avatarUrl = "https://example.com/avatar.png";
    private OffsetDateTime publishedAt;
    private PostLanguage language = PostLanguage.ENGLISH;
    private User user;
    private Set<Category> categories = new HashSet<>();

    public static PostBuilder aPost() {
        return new PostBuilder();
    }

    public PostBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public PostBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public PostBuilder withSlug(String slug) {
        this.slug = slug;
        return this;
    }

    public PostBuilder withStatus(PostStatus status) {
        this.status = status;
        return this;
    }

    public PostBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public PostBuilder withContent(String content) {
        this.content = content;
        return this;
    }

    public PostBuilder withAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public PostBuilder withPublishedAt(OffsetDateTime publishedAt) {
        this.publishedAt = publishedAt;
        return this;
    }

    public PostBuilder withLanguage(PostLanguage language) {
        this.language = language;
        return this;
    }

    public PostBuilder withUser(User user) {
        this.user = user;
        return this;
    }

    public PostBuilder withCategories(Set<Category> categories) {
        this.categories = categories;
        return this;
    }

    public PostBuilder addCategory(Category category) {
        this.categories.add(category);
        return this;
    }

    public Post build() {
        Post post =
                Post.builder()
                        .title(title)
                        .slug(slug)
                        .status(status)
                        .description(description)
                        .content(content)
                        .avatarUrl(avatarUrl)
                        .publishedAt(publishedAt)
                        .language(language)
                        .user(user)
                        .categories(categories)
                        .build();

        if (id != null) {
            post.setId(id);
        }

        return post;
    }
}

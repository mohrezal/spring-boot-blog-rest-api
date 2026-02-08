package com.github.mohrezal.api.domains.posts.dtos;

import com.github.mohrezal.api.domains.posts.enums.PostLanguage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

public record CreatePostRequest(
        @NotBlank @Size(max = 255) String title,
        @NotBlank String content,
        @NotBlank String avatarUrl,
        @NotEmpty Set<UUID> categoryIds,
        @Size(max = 300) String description,
        @NotNull PostLanguage language) {}

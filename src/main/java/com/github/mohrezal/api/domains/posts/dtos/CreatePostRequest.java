package com.github.mohrezal.api.domains.posts.dtos;

import com.github.mohrezal.api.domains.posts.enums.PostLanguage;
import com.github.mohrezal.api.shared.enums.MessageKey;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

public record CreatePostRequest(
        @NotBlank(message = MessageKey.SHARED_NOT_BLANK)
                @Size(max = 255, message = MessageKey.SHARED_VALIDATION_SIZE_MAX)
                String title,
        @NotBlank(message = MessageKey.SHARED_NOT_BLANK) String content,
        @NotBlank(message = MessageKey.SHARED_NOT_BLANK) String avatarUrl,
        @NotEmpty(message = MessageKey.SHARED_NOT_EMPTY) Set<UUID> categoryIds,
        @Size(max = 300, message = MessageKey.SHARED_VALIDATION_SIZE_MAX) String description,
        @NotNull(message = MessageKey.SHARED_NOT_NULL) PostLanguage language) {}

package com.github.mohrezal.api.domains.posts.dtos;

import com.github.mohrezal.api.shared.constants.RegexUtils;
import com.github.mohrezal.api.shared.enums.MessageKey;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

public record UpdatePostRequest(
        @NotBlank(message = MessageKey.SHARED_NOT_BLANK)
                @Size(max = 255, message = MessageKey.SHARED_VALIDATION_SIZE_MAX)
                String title,
        @NotBlank(message = MessageKey.SHARED_NOT_BLANK) String content,
        @NotBlank(message = MessageKey.SHARED_NOT_BLANK) String avatarUrl,
        @NotEmpty(message = MessageKey.SHARED_NOT_EMPTY) Set<UUID> categoryIds,
        @Size(max = 300, message = MessageKey.SHARED_VALIDATION_SIZE_MAX) String description,
        @NotBlank(message = MessageKey.SHARED_NOT_BLANK)
                @Pattern(
                        regexp = RegexUtils.SLUG_PATTERN,
                        message = MessageKey.POST_SLUG_INVALID_FORMAT_KEY)
                String slug) {}

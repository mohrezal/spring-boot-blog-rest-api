package com.github.mohrezal.api.domains.posts.dtos;

import com.github.mohrezal.common.constants.MessageKey;
import com.github.mohrezal.common.constants.RegexUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

public record UpdatePostRequest(
        @NotBlank(message = MessageKey.Shared.Validation.NOT_BLANK)
                @Size(max = 255, message = MessageKey.Shared.Validation.SIZE_MAX)
                String title,
        @NotBlank(message = MessageKey.Shared.Validation.NOT_BLANK) String content,
        @NotBlank(message = MessageKey.Shared.Validation.NOT_BLANK) String avatarUrl,
        @NotEmpty(message = MessageKey.Shared.Validation.NOT_EMPTY) Set<UUID> categoryIds,
        @Size(max = 300, message = MessageKey.Shared.Validation.SIZE_MAX) String description,
        @NotBlank(message = MessageKey.Shared.Validation.NOT_BLANK)
                @Pattern(
                        regexp = RegexUtils.SLUG_PATTERN,
                        message = MessageKey.Post.Error.SLUG_INVALID_FORMAT_KEY)
                String slug) {}

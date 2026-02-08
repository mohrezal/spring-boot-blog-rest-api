package com.github.mohrezal.api.domains.posts.dtos;

import com.github.mohrezal.api.shared.constants.RegexUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

public record UpdatePostRequest(
        @NotBlank @Size(max = 255) String title,
        @NotBlank String content,
        @NotBlank String avatarUrl,
        @NotEmpty Set<UUID> categoryIds,
        @Size(max = 300) String description,
        @NotBlank @Pattern(regexp = RegexUtils.SLUG_PATTERN) String slug) {}

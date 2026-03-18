package com.github.mohrezal.api.domains.categories.dtos;

import com.github.mohrezal.api.shared.enums.MessageKey;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateCategoryRequest(
        @NotBlank(message = MessageKey.SHARED_NOT_BLANK)
                @Size(max = 255, message = MessageKey.SHARED_VALIDATION_SIZE_MAX)
                String name,
        @NotBlank(message = MessageKey.SHARED_NOT_BLANK) String slug,
        @Size(max = 300, message = MessageKey.SHARED_VALIDATION_SIZE_MAX) String description,
        UUID parentId) {}

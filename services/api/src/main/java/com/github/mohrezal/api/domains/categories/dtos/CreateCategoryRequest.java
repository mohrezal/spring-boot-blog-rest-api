package com.github.mohrezal.api.domains.categories.dtos;

import com.github.mohrezal.common.constants.MessageKey;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateCategoryRequest(
        @NotBlank(message = MessageKey.Shared.Validation.NOT_BLANK)
                @Size(max = 255, message = MessageKey.Shared.Validation.SIZE_MAX)
                String name,
        @NotBlank(message = MessageKey.Shared.Validation.NOT_BLANK) String slug,
        @Size(max = 300, message = MessageKey.Shared.Validation.SIZE_MAX) String description,
        UUID parentId) {}

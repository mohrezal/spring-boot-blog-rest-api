package com.github.mohrezal.api.domains.users.dtos;

import com.github.mohrezal.api.shared.constants.RegexUtils;
import com.github.mohrezal.api.shared.enums.MessageKey;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(
        @Size(min = 2, max = 50, message = MessageKey.Validation.SHARED_SIZE)
                @Pattern(
                        regexp = RegexUtils.NAME_PATTERN,
                        message = MessageKey.Validation.USER_NAME_PATTERN)
                String firstName,
        @Size(min = 2, max = 50, message = MessageKey.Validation.SHARED_SIZE)
                @Pattern(
                        regexp = RegexUtils.NAME_PATTERN,
                        message = MessageKey.Validation.USER_NAME_PATTERN)
                String lastName,
        @Size(max = 500, message = MessageKey.Validation.SHARED_VALIDATION_SIZE_MAX) String bio) {}

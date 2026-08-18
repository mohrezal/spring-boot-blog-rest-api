package com.github.mohrezal.api.domains.users.dtos;

import com.github.mohrezal.common.constants.MessageKey;
import com.github.mohrezal.common.constants.RegexUtils;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(
        @Size(min = 2, max = 50, message = MessageKey.Shared.Validation.SIZE)
                @Pattern(
                        regexp = RegexUtils.NAME_PATTERN,
                        message = MessageKey.User.Validation.NAME_PATTERN)
                String firstName,
        @Size(min = 2, max = 50, message = MessageKey.Shared.Validation.SIZE)
                @Pattern(
                        regexp = RegexUtils.NAME_PATTERN,
                        message = MessageKey.User.Validation.NAME_PATTERN)
                String lastName,
        @Size(max = 500, message = MessageKey.Shared.Validation.SIZE_MAX) String bio) {}

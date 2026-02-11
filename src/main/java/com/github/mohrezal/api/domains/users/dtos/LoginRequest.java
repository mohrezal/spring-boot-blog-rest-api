package com.github.mohrezal.api.domains.users.dtos;

import com.github.mohrezal.api.shared.constants.RegexUtils;
import com.github.mohrezal.api.shared.enums.MessageKey;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = MessageKey.SHARED_NOT_BLANK)
                @Email(message = MessageKey.SHARED_EMAIL)
                @Size(max = 100, message = MessageKey.SHARED_VALIDATION_SIZE_MAX)
                String email,
        @NotBlank(message = MessageKey.SHARED_NOT_BLANK)
                @Size(min = 8, max = 64, message = MessageKey.SHARED_SIZE)
                @Pattern(
                        regexp = RegexUtils.PASSWORD_PATTERN,
                        message = MessageKey.USER_PASSWORD_PATTERN)
                String password) {}

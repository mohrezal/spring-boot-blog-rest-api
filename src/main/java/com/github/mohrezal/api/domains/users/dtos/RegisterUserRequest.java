package com.github.mohrezal.api.domains.users.dtos;

import com.github.mohrezal.api.shared.constants.RegexUtils;
import com.github.mohrezal.api.shared.enums.MessageKey;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank(message = MessageKey.Validation.SHARED_NOT_BLANK)
                @Size(min = 2, max = 50, message = MessageKey.Validation.SHARED_SIZE)
                @Pattern(
                        regexp = RegexUtils.NAME_PATTERN,
                        message = MessageKey.Validation.USER_NAME_PATTERN)
                String firstName,
        @NotBlank(message = MessageKey.Validation.SHARED_NOT_BLANK)
                @Size(min = 2, max = 50, message = MessageKey.Validation.SHARED_SIZE)
                @Pattern(
                        regexp = RegexUtils.NAME_PATTERN,
                        message = MessageKey.Validation.USER_NAME_PATTERN)
                String lastName,
        @NotBlank(message = MessageKey.Validation.SHARED_NOT_BLANK)
                @Email(message = MessageKey.Validation.SHARED_EMAIL)
                @Size(max = 100, message = MessageKey.Validation.SHARED_VALIDATION_SIZE_MAX)
                String email,
        @NotBlank(message = MessageKey.Validation.SHARED_NOT_BLANK)
                @Size(min = 5, max = 30, message = MessageKey.Validation.SHARED_SIZE)
                @Pattern(
                        regexp = RegexUtils.HANDLE_PATTERN,
                        message = MessageKey.Validation.USER_HANDLE_PATTERN)
                String handle,
        @NotBlank(message = MessageKey.Validation.SHARED_NOT_BLANK)
                @Size(min = 8, max = 64, message = MessageKey.Validation.SHARED_SIZE)
                @Pattern(
                        regexp = RegexUtils.PASSWORD_PATTERN,
                        message = MessageKey.Validation.USER_PASSWORD_PATTERN)
                String password,
        @Size(max = 500, message = MessageKey.Validation.SHARED_VALIDATION_SIZE_MAX) String bio) {}

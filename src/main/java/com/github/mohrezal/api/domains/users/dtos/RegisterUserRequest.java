package com.github.mohrezal.api.domains.users.dtos;

import com.github.mohrezal.api.shared.constants.RegexUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank
                @Size(min = 2, max = 50)
                @Pattern(
                        regexp = RegexUtils.NAME_PATTERN,
                        message = "Only letters, spaces, apostrophes, hyphens")
                String firstName,
        @NotBlank
                @Size(min = 2, max = 50)
                @Pattern(
                        regexp = RegexUtils.NAME_PATTERN,
                        message = "Only letters, spaces, apostrophes, hyphens")
                String lastName,
        @NotBlank @Email @Size(max = 100) String email,
        @NotBlank
                @Size(min = 5, max = 30)
                @Pattern(
                        regexp = RegexUtils.HANDLE_PATTERN,
                        message = "Only lowercase letters, numbers, and underscores")
                String handle,
        @NotBlank
                @Size(min = 8, max = 64)
                @Pattern(
                        regexp = RegexUtils.PASSWORD_PATTERN,
                        message = "Must include uppercase, number & special character")
                String password,
        @Size(max = 500) String bio) {}

package com.github.mohrezal.api.domains.users.dtos;

import com.github.mohrezal.api.shared.constants.RegexUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank @Email @Size(max = 100) String email,
        @NotBlank
                @Size(min = 8, max = 64)
                @Pattern(
                        regexp = RegexUtils.PASSWORD_PATTERN,
                        message = "Must include uppercase, number & special character")
                String password) {}

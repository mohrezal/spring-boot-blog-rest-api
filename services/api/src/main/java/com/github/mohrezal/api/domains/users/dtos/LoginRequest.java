package com.github.mohrezal.api.domains.users.dtos;

import com.github.mohrezal.common.constants.MessageKey;
import com.github.mohrezal.common.constants.RegexUtils;
import com.github.mohrezal.common.email.EmailNormalizer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = MessageKey.Shared.Validation.NOT_BLANK)
                @Email(message = MessageKey.Shared.Validation.EMAIL_KEY)
                @Size(max = 100, message = MessageKey.Shared.Validation.SIZE_MAX)
                String email,
        @NotBlank(message = MessageKey.Shared.Validation.NOT_BLANK)
                @Size(min = 8, max = 64, message = MessageKey.Shared.Validation.SIZE)
                @Pattern(
                        regexp = RegexUtils.PASSWORD_PATTERN,
                        message = MessageKey.User.Validation.PASSWORD_PATTERN)
                String password) {
    public LoginRequest {
        email = EmailNormalizer.normalize(email);
    }
}

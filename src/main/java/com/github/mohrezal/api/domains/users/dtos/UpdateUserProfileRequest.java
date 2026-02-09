package com.github.mohrezal.api.domains.users.dtos;

import com.github.mohrezal.api.shared.constants.RegexUtils;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(
        @Size(min = 2, max = 50)
                @Pattern(
                        regexp = RegexUtils.NAME_PATTERN,
                        message = "Only letters, spaces, apostrophes, hyphens")
                String firstName,
        @Size(min = 2, max = 50)
                @Pattern(
                        regexp = RegexUtils.NAME_PATTERN,
                        message = "Only letters, spaces, apostrophes, hyphens")
                String lastName,
        @Size(max = 500) String bio) {}

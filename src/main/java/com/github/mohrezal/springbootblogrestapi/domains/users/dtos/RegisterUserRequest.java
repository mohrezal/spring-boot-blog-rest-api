package com.github.mohrezal.springbootblogrestapi.domains.users.dtos;

import com.github.mohrezal.springbootblogrestapi.shared.constants.RegexUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class RegisterUserRequest {

    @NotBlank
    @Size(min = 2, max = 50)
    @Pattern(
            regexp = RegexUtils.NAME_PATTERN,
            message = "Only letters, spaces, apostrophes, hyphens")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50)
    @Pattern(
            regexp = RegexUtils.NAME_PATTERN,
            message = "Only letters, spaces, apostrophes, hyphens")
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(min = 5, max = 30)
    @Pattern(
            regexp = RegexUtils.HANDLE_PATTERN,
            message = "Only lowercase letters, numbers, and underscores")
    private String handle;

    @NotBlank
    @Size(min = 8, max = 64)
    @Pattern(
            regexp = RegexUtils.PASSWORD_PATTERN,
            message = "Must include uppercase, number & special character")
    private String password;

    @Size(max = 500)
    private String bio;

    @Size(max = 255)
    private String avatarUrl;
}

package com.github.mohrezal.springbootblogrestapi.domains.users.dtos;

import com.github.mohrezal.springbootblogrestapi.shared.constants.RegexUtils;
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
public class UpdateUserProfileRequest {

    @Size(min = 2, max = 50)
    @Pattern(
            regexp = RegexUtils.NAME_PATTERN,
            message = "Only letters, spaces, apostrophes, hyphens")
    private String firstName;

    @Size(min = 2, max = 50)
    @Pattern(
            regexp = RegexUtils.NAME_PATTERN,
            message = "Only letters, spaces, apostrophes, hyphens")
    private String lastName;

    @Size(max = 500)
    private String bio;
}

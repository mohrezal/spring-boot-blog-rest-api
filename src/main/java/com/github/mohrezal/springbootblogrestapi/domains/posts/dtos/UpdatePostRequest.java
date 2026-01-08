package com.github.mohrezal.springbootblogrestapi.domains.posts.dtos;

import com.github.mohrezal.springbootblogrestapi.shared.constants.RegexUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatePostRequest {

    @NotBlank
    @Size(max = 255)
    private String title;

    @NotBlank private String content;

    @NotBlank private String avatarUrl;

    @NotEmpty private Set<UUID> categoryIds;

    @Size(max = 300)
    private String description;

    @NotBlank
    @Pattern(regexp = RegexUtils.SLUG_PATTERN)
    private String slug;
}

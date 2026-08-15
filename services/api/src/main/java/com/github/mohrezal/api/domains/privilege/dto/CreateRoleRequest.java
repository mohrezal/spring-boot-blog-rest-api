package com.github.mohrezal.api.domains.privilege.dto;

import com.github.mohrezal.common.constants.RegexUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

public record CreateRoleRequest(
        @NotBlank @Size(max = 100) @Pattern(regexp = RegexUtils.KEY_PATTERN) String key,
        @NotBlank @Size(max = 150) String name,
        @NotNull Set<@NotNull UUID> permissionIds) {}

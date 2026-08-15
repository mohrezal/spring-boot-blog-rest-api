package com.github.mohrezal.api.domains.privilege.dto;

import java.util.UUID;

public record PermissionSummary(
        UUID id, String key, String name, String service, Boolean enabled) {}

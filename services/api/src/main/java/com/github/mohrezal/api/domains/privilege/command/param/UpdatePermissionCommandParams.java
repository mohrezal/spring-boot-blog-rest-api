package com.github.mohrezal.api.domains.privilege.command.param;

import com.github.mohrezal.api.domains.privilege.dto.UpdatePermissionRequest;
import java.util.UUID;

public record UpdatePermissionCommandParams(UUID permissionId, UpdatePermissionRequest request) {}

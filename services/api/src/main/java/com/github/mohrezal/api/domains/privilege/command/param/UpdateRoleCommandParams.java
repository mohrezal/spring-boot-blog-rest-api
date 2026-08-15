package com.github.mohrezal.api.domains.privilege.command.param;

import com.github.mohrezal.api.domains.privilege.dto.UpdateRoleRequest;
import java.util.UUID;

public record UpdateRoleCommandParams(UUID roleId, UpdateRoleRequest request) {}

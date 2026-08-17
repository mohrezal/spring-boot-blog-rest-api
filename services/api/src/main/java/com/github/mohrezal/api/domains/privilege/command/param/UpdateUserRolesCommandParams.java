package com.github.mohrezal.api.domains.privilege.command.param;

import com.github.mohrezal.api.domains.privilege.dto.UpdateUserRolesRequest;
import java.util.UUID;

public record UpdateUserRolesCommandParams(UUID userId, UpdateUserRolesRequest request) {}

package com.github.mohrezal.api.domains.privilege.controller;

import com.github.mohrezal.api.config.Routes;
import com.github.mohrezal.api.domains.privilege.command.UpdatePermissionCommand;
import com.github.mohrezal.api.domains.privilege.command.param.UpdatePermissionCommandParams;
import com.github.mohrezal.api.domains.privilege.constant.Permissions;
import com.github.mohrezal.api.domains.privilege.dto.PermissionSummary;
import com.github.mohrezal.api.domains.privilege.dto.UpdatePermissionRequest;
import com.github.mohrezal.api.domains.privilege.query.GetPermissionQuery;
import com.github.mohrezal.api.domains.privilege.query.GetPermissionsQuery;
import com.github.mohrezal.api.domains.privilege.query.param.GetPermissionQueryParams;
import com.github.mohrezal.api.domains.privilege.query.param.GetPermissionsQueryParams;
import com.github.mohrezal.api.shared.annotations.RequiresPermission;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.Privilege.PERMISSIONS)
@RequiredArgsConstructor
@Tag(name = "Privilege")
public class PermissionController {

    private final GetPermissionsQuery getPermissionsQuery;
    private final GetPermissionQuery getPermissionQuery;
    private final UpdatePermissionCommand updatePermissionCommand;

    @RequiresPermission(Permissions.BLOG_PRIVILEGE_PERMISSIONS_READ)
    @GetMapping
    public ResponseEntity<List<PermissionSummary>> getPermissions() {
        var response = getPermissionsQuery.execute(new GetPermissionsQueryParams());
        return ResponseEntity.ok(response);
    }

    @RequiresPermission(Permissions.BLOG_PRIVILEGE_PERMISSIONS_READ)
    @GetMapping(Routes.Privilege.PERMISSION)
    public ResponseEntity<PermissionSummary> getPermission(@PathVariable UUID id) {
        var response = getPermissionQuery.execute(new GetPermissionQueryParams(id));
        return ResponseEntity.ok(response);
    }

    @RequiresPermission(Permissions.BLOG_PRIVILEGE_PERMISSIONS_UPDATE)
    @PutMapping(Routes.Privilege.PERMISSION)
    public ResponseEntity<PermissionSummary> update(
            @PathVariable UUID id, @Valid @RequestBody UpdatePermissionRequest body) {
        var response = updatePermissionCommand.execute(new UpdatePermissionCommandParams(id, body));
        return ResponseEntity.ok(response);
    }
}

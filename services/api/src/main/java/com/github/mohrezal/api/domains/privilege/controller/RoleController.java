package com.github.mohrezal.api.domains.privilege.controller;

import com.github.mohrezal.api.config.Routes;
import com.github.mohrezal.api.domains.privilege.command.CreateRoleCommand;
import com.github.mohrezal.api.domains.privilege.command.DeleteRoleCommand;
import com.github.mohrezal.api.domains.privilege.command.UpdateRoleCommand;
import com.github.mohrezal.api.domains.privilege.command.UpdateUserRolesCommand;
import com.github.mohrezal.api.domains.privilege.command.param.CreateRoleCommandParams;
import com.github.mohrezal.api.domains.privilege.command.param.DeleteRoleCommandParams;
import com.github.mohrezal.api.domains.privilege.command.param.UpdateRoleCommandParams;
import com.github.mohrezal.api.domains.privilege.command.param.UpdateUserRolesCommandParams;
import com.github.mohrezal.api.domains.privilege.constant.Permissions;
import com.github.mohrezal.api.domains.privilege.dto.CreateRoleRequest;
import com.github.mohrezal.api.domains.privilege.dto.RoleSummary;
import com.github.mohrezal.api.domains.privilege.dto.UpdateRoleRequest;
import com.github.mohrezal.api.domains.privilege.dto.UpdateUserRolesRequest;
import com.github.mohrezal.api.domains.privilege.query.GetRoleQuery;
import com.github.mohrezal.api.domains.privilege.query.GetRolesQuery;
import com.github.mohrezal.api.domains.privilege.query.GetUserRolesQuery;
import com.github.mohrezal.api.domains.privilege.query.param.GetRoleQueryParams;
import com.github.mohrezal.api.domains.privilege.query.param.GetRolesQueryParams;
import com.github.mohrezal.api.domains.privilege.query.param.GetUserRolesQueryParams;
import com.github.mohrezal.api.shared.annotations.RequiresPermission;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.Privilege.ROLES)
@RequiredArgsConstructor
@Tag(name = "Privilege")
public class RoleController {

    private final GetRolesQuery getRolesQuery;
    private final GetRoleQuery getRoleQuery;
    private final GetUserRolesQuery getUserRolesQuery;
    private final CreateRoleCommand createRoleCommand;
    private final DeleteRoleCommand deleteRoleCommand;
    private final UpdateRoleCommand updateRoleCommand;
    private final UpdateUserRolesCommand updateUserRolesCommand;

    @RequiresPermission(Permissions.BLOG_PRIVILEGE_ROLES_READ)
    @GetMapping
    public ResponseEntity<List<RoleSummary>> getRoles() {
        var response = getRolesQuery.execute(new GetRolesQueryParams());
        return ResponseEntity.ok(response);
    }

    @RequiresPermission(Permissions.BLOG_PRIVILEGE_ROLES_READ)
    @GetMapping(Routes.Privilege.ROLE)
    public ResponseEntity<RoleSummary> getRole(@PathVariable UUID id) {
        var response = getRoleQuery.execute(new GetRoleQueryParams(id));
        return ResponseEntity.ok(response);
    }

    @RequiresPermission(Permissions.BLOG_PRIVILEGE_ROLES_CREATE)
    @PostMapping
    public ResponseEntity<RoleSummary> create(@Valid @RequestBody CreateRoleRequest body) {
        var response = createRoleCommand.execute(new CreateRoleCommandParams(body));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequiresPermission(Permissions.BLOG_PRIVILEGE_ROLES_UPDATE)
    @PutMapping(Routes.Privilege.ROLE)
    public ResponseEntity<RoleSummary> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateRoleRequest body) {
        var response = updateRoleCommand.execute(new UpdateRoleCommandParams(id, body));
        return ResponseEntity.ok(response);
    }

    @RequiresPermission(Permissions.BLOG_PRIVILEGE_USERS_ASSIGN_ROLES)
    @PutMapping(Routes.Privilege.ROLE_ASSIGNMENTS)
    public ResponseEntity<List<RoleSummary>> updateUserRoles(
            @PathVariable UUID userId, @Valid @RequestBody UpdateUserRolesRequest body) {
        var response =
                updateUserRolesCommand.execute(new UpdateUserRolesCommandParams(userId, body));
        return ResponseEntity.ok(response);
    }

    @RequiresPermission(Permissions.BLOG_PRIVILEGE_ROLES_READ)
    @GetMapping(Routes.Privilege.ROLE_ASSIGNMENTS)
    public ResponseEntity<List<RoleSummary>> getUserRoles(@PathVariable UUID userId) {
        var response = getUserRolesQuery.execute(new GetUserRolesQueryParams(userId));
        return ResponseEntity.ok(response);
    }

    @RequiresPermission(Permissions.BLOG_PRIVILEGE_ROLES_DELETE)
    @DeleteMapping(Routes.Privilege.ROLE)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteRoleCommand.execute(new DeleteRoleCommandParams(id));
        return ResponseEntity.noContent().build();
    }
}

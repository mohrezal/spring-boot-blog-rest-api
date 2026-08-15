package com.github.mohrezal.api.domains.privilege.controller;

import com.github.mohrezal.api.config.Routes;
import com.github.mohrezal.api.domains.privilege.constant.Permissions;
import com.github.mohrezal.api.domains.privilege.dto.RoleSummary;
import com.github.mohrezal.api.domains.privilege.query.GetRoleQuery;
import com.github.mohrezal.api.domains.privilege.query.GetRolesQuery;
import com.github.mohrezal.api.domains.privilege.query.param.GetRoleQueryParams;
import com.github.mohrezal.api.domains.privilege.query.param.GetRolesQueryParams;
import com.github.mohrezal.api.shared.annotations.RequiresPermission;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.Privilege.ROLES)
@RequiredArgsConstructor
@Tag(name = "Privilege")
public class RoleController {

    private final GetRolesQuery getRolesQuery;
    private final GetRoleQuery getRoleQuery;

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
}

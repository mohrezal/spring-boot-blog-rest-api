package com.github.mohrezal.api.domains.privilege.command;

import com.github.mohrezal.api.domains.privilege.command.param.UpdateRoleCommandParams;
import com.github.mohrezal.api.domains.privilege.dto.RoleSummary;
import com.github.mohrezal.api.domains.privilege.exception.types.OwnerRoleCannotBeUpdatedException;
import com.github.mohrezal.api.domains.privilege.exception.types.PermissionNotFoundException;
import com.github.mohrezal.api.domains.privilege.exception.types.RoleNotFoundException;
import com.github.mohrezal.api.domains.privilege.mapper.RoleMapper;
import com.github.mohrezal.api.domains.privilege.model.RolePermission;
import com.github.mohrezal.api.domains.privilege.repository.PermissionRepository;
import com.github.mohrezal.api.domains.privilege.repository.RoleRepository;
import com.github.mohrezal.api.domains.privilege.repository.UserRoleRepository;
import com.github.mohrezal.api.domains.privilege.service.UserPrivilegeVersionService;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.config.ApplicationProperties;
import com.github.mohrezal.api.shared.interfaces.Command;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateRoleCommand implements Command<UpdateRoleCommandParams, RoleSummary> {

    private final ApplicationProperties applicationProperties;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleMapper roleMapper;
    private final UserPrivilegeVersionService userPrivilegeVersionService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RoleSummary execute(UpdateRoleCommandParams params) {
        var role =
                roleRepository
                        .findByIdWithPermissions(params.roleId())
                        .orElseThrow(RoleNotFoundException::new);

        if (role.getKey().equals(applicationProperties.privilege().role().owner().key())) {
            throw new OwnerRoleCannotBeUpdatedException();
        }

        var request = params.request();
        var permissionIds = request.permissionIds();
        var permissions = permissionRepository.findAllById(permissionIds);

        if (permissions.size() != permissionIds.size()) {
            throw new PermissionNotFoundException();
        }

        role.setName(request.name());
        role.setEnabled(request.enabled());
        role.getPermissions()
                .removeIf(
                        rolePermission ->
                                !permissionIds.contains(rolePermission.getPermission().getId()));

        var assignedPermissionIds =
                role.getPermissions().stream()
                        .map(rolePermission -> rolePermission.getPermission().getId())
                        .collect(Collectors.toSet());

        permissions.stream()
                .filter(permission -> !assignedPermissionIds.contains(permission.getId()))
                .map(
                        permission ->
                                RolePermission.builder().role(role).permission(permission).build())
                .forEach(role.getPermissions()::add);

        var savedRole = roleRepository.saveAndFlush(role);
        userRoleRepository.findUserIdsByRoleId(savedRole.getId()).stream()
                .map(
                        userId ->
                                userRepository
                                        .findById(userId)
                                        .orElseThrow(UserNotFoundException::new))
                .forEach(userPrivilegeVersionService::increment);
        log.info(
                "Role updated. roleId={}, permissionCount={}",
                savedRole.getId(),
                savedRole.getPermissions().size());

        return roleMapper.toSummary(savedRole);
    }
}

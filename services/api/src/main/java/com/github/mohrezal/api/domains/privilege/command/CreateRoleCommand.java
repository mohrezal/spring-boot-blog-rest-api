package com.github.mohrezal.api.domains.privilege.command;

import com.github.mohrezal.api.domains.privilege.command.param.CreateRoleCommandParams;
import com.github.mohrezal.api.domains.privilege.dto.RoleSummary;
import com.github.mohrezal.api.domains.privilege.exception.types.PermissionNotFoundException;
import com.github.mohrezal.api.domains.privilege.exception.types.RoleKeyAlreadyExistsException;
import com.github.mohrezal.api.domains.privilege.mapper.RoleMapper;
import com.github.mohrezal.api.domains.privilege.model.Role;
import com.github.mohrezal.api.domains.privilege.model.RolePermission;
import com.github.mohrezal.api.domains.privilege.repository.PermissionRepository;
import com.github.mohrezal.api.domains.privilege.repository.RoleRepository;
import com.github.mohrezal.api.shared.interfaces.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateRoleCommand implements Command<CreateRoleCommandParams, RoleSummary> {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    @Override
    public void validate(CreateRoleCommandParams params) {
        if (roleRepository.existsByKey(params.request().key())) {
            throw new RoleKeyAlreadyExistsException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RoleSummary execute(CreateRoleCommandParams params) {
        validate(params);

        var request = params.request();
        var role = Role.builder().key(request.key()).name(request.name()).build();
        var permissions = permissionRepository.findAllById(request.permissionIds());

        if (permissions.size() != request.permissionIds().size()) {
            throw new PermissionNotFoundException();
        }

        permissions.forEach(
                permission ->
                        role.getPermissions()
                                .add(
                                        RolePermission.builder()
                                                .role(role)
                                                .permission(permission)
                                                .build()));

        try {
            var savedRole = roleRepository.saveAndFlush(role);
            log.info("Role created. roleId={}, key={}", savedRole.getId(), savedRole.getKey());
            return roleMapper.toSummary(savedRole);
        } catch (DataIntegrityViolationException exception) {
            throw new RoleKeyAlreadyExistsException();
        }
    }
}

package com.github.mohrezal.api.domains.privilege.command;

import com.github.mohrezal.api.domains.privilege.command.param.UpdatePermissionCommandParams;
import com.github.mohrezal.api.domains.privilege.constant.Permissions;
import com.github.mohrezal.api.domains.privilege.dto.PermissionSummary;
import com.github.mohrezal.api.domains.privilege.exception.types.PermissionNotFoundException;
import com.github.mohrezal.api.domains.privilege.exception.types.ProtectedPermissionCannotBeDisabledException;
import com.github.mohrezal.api.domains.privilege.mapper.PermissionMapper;
import com.github.mohrezal.api.domains.privilege.repository.PermissionRepository;
import com.github.mohrezal.api.domains.privilege.repository.UserRoleRepository;
import com.github.mohrezal.api.domains.privilege.service.UserPrivilegeVersionService;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.interfaces.Command;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdatePermissionCommand
        implements Command<UpdatePermissionCommandParams, PermissionSummary> {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final UserPrivilegeVersionService userPrivilegeVersionService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PermissionSummary execute(UpdatePermissionCommandParams params) {
        var permission =
                permissionRepository
                        .findById(params.permissionId())
                        .orElseThrow(PermissionNotFoundException::new);
        var request = params.request();

        if (Permissions.BLOG_PRIVILEGE_PERMISSIONS_UPDATE.equals(permission.getKey())
                && !request.enabled()) {
            throw new ProtectedPermissionCannotBeDisabledException();
        }

        var enabledChanged = !Objects.equals(permission.getEnabled(), request.enabled());
        permission.setName(request.name());
        permission.setEnabled(request.enabled());

        var savedPermission = permissionRepository.save(permission);
        if (enabledChanged) {
            userRoleRepository.findUserIdsByPermissionId(savedPermission.getId()).stream()
                    .map(
                            userId ->
                                    userRepository
                                            .findById(userId)
                                            .orElseThrow(UserNotFoundException::new))
                    .forEach(userPrivilegeVersionService::increment);
        }
        log.info(
                "Permission updated. permissionId={}, key={}",
                savedPermission.getId(),
                savedPermission.getKey());
        return permissionMapper.toSummary(savedPermission);
    }
}

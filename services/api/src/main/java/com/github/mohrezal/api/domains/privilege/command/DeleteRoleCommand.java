package com.github.mohrezal.api.domains.privilege.command;

import com.github.mohrezal.api.domains.privilege.command.param.DeleteRoleCommandParams;
import com.github.mohrezal.api.domains.privilege.exception.types.ConfiguredRoleCannotBeDeletedException;
import com.github.mohrezal.api.domains.privilege.exception.types.RoleAssignedToUsersException;
import com.github.mohrezal.api.domains.privilege.exception.types.RoleNotFoundException;
import com.github.mohrezal.api.domains.privilege.repository.RoleRepository;
import com.github.mohrezal.api.domains.privilege.repository.UserRoleRepository;
import com.github.mohrezal.api.shared.config.ApplicationProperties;
import com.github.mohrezal.api.shared.interfaces.Command;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteRoleCommand implements Command<DeleteRoleCommandParams, Void> {

    private final ApplicationProperties applicationProperties;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void execute(DeleteRoleCommandParams params) {
        var role = roleRepository.findById(params.roleId()).orElseThrow(RoleNotFoundException::new);
        var configuredRoles = applicationProperties.privilege().role();
        var configuredRoleKeys =
                Set.of(configuredRoles.owner().key(), configuredRoles.user().key());

        if (configuredRoleKeys.contains(role.getKey())) {
            throw new ConfiguredRoleCannotBeDeletedException();
        }

        if (userRoleRepository.existsByRole(role)) {
            throw new RoleAssignedToUsersException();
        }

        roleRepository.delete(role);
        log.info("Role deleted. roleId={}, key={}", role.getId(), role.getKey());
        return null;
    }
}

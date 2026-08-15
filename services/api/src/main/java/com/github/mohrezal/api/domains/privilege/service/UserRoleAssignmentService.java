package com.github.mohrezal.api.domains.privilege.service;

import com.github.mohrezal.api.domains.privilege.model.UserRole;
import com.github.mohrezal.api.domains.privilege.repository.RoleRepository;
import com.github.mohrezal.api.domains.privilege.repository.UserRoleRepository;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.shared.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRoleAssignmentService {

    private final ApplicationProperties applicationProperties;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Transactional(rollbackFor = Exception.class)
    public void assignConfiguredUserRole(User user) {
        var roleKey = applicationProperties.privilege().role().user().key();
        var role =
                roleRepository
                        .findByKey(roleKey)
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "Configured user role not found: " + roleKey));

        if (userRoleRepository.existsByUserAndRole(user, role)) {
            return;
        }

        userRoleRepository.save(UserRole.builder().user(user).role(role).build());
    }
}

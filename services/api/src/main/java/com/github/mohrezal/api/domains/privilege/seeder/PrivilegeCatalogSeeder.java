package com.github.mohrezal.api.domains.privilege.seeder;

import com.github.mohrezal.api.domains.privilege.constant.PermissionCatalog;
import com.github.mohrezal.api.domains.privilege.constant.PermissionCatalog.Definition;
import com.github.mohrezal.api.domains.privilege.constant.Permissions;
import com.github.mohrezal.api.domains.privilege.model.Permission;
import com.github.mohrezal.api.domains.privilege.model.Role;
import com.github.mohrezal.api.domains.privilege.model.RolePermission;
import com.github.mohrezal.api.domains.privilege.model.UserRole;
import com.github.mohrezal.api.domains.privilege.repository.PermissionRepository;
import com.github.mohrezal.api.domains.privilege.repository.RolePermissionRepository;
import com.github.mohrezal.api.domains.privilege.repository.RoleRepository;
import com.github.mohrezal.api.domains.privilege.repository.UserRoleRepository;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.config.ApplicationProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Order(1)
@Component
@Profile("seed-privilege")
@RequiredArgsConstructor
public class PrivilegeCatalogSeeder implements CommandLineRunner {

    private final ApplicationProperties applicationProperties;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final ConfigurableApplicationContext applicationContext;

    @Override
    public void run(String... args) {
        seedPrivilegeCatalog();
        log.info("Privilege catalog seeding completed.");
        System.exit(SpringApplication.exit(applicationContext, () -> 0));
    }

    @Transactional(rollbackFor = Exception.class)
    public void seedPrivilegeCatalog() {
        var roles = applicationProperties.privilege().role();
        seedRole(roles.owner(), PermissionCatalog.ALL);
        var userRole = seedRole(roles.user(), PermissionCatalog.USER);
        assignConfiguredUserRoleToUsersWithoutRoles(userRole);
    }

    private Role seedRole(
            ApplicationProperties.Privilege.Properties properties, List<Definition> permissions) {
        var role =
                roleRepository
                        .findByKey(properties.key())
                        .orElseGet(
                                () ->
                                        roleRepository.save(
                                                Role.builder()
                                                        .key(properties.key())
                                                        .name(properties.name())
                                                        .build()));

        seedPermissions(role, permissions);
        return role;
    }

    private void seedPermissions(Role role, List<Definition> permissionDefinitions) {
        var rolePermissions =
                permissionDefinitions.stream()
                        .map(this::seedPermission)
                        .filter(
                                permission ->
                                        !rolePermissionRepository.existsByRoleAndPermission(
                                                role, permission))
                        .map(
                                permission ->
                                        RolePermission.builder()
                                                .role(role)
                                                .permission(permission)
                                                .build())
                        .toList();

        rolePermissionRepository.saveAll(rolePermissions);
    }

    private Permission seedPermission(Definition definition) {
        var permission =
                permissionRepository
                        .findByKey(definition.key())
                        .orElseGet(
                                () ->
                                        permissionRepository.save(
                                                Permission.builder()
                                                        .key(definition.key())
                                                        .name(definition.name())
                                                        .service(definition.service())
                                                        .build()));

        if (Permissions.BLOG_PRIVILEGE_PERMISSIONS_UPDATE.equals(permission.getKey())
                && Boolean.FALSE.equals(permission.getEnabled())) {
            permission.setEnabled(true);
            log.warn("Re-enabling protected permission. key={}", permission.getKey());
            return permissionRepository.save(permission);
        }

        return permission;
    }

    private void assignConfiguredUserRoleToUsersWithoutRoles(Role userRole) {
        for (var user : userRepository.findAll()) {
            if (!userRoleRepository.existsByUser(user)) {
                userRoleRepository.save(UserRole.builder().user(user).role(userRole).build());
            }
        }
    }
}

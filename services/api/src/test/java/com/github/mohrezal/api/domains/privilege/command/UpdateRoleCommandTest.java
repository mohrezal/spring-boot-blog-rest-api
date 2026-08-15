package com.github.mohrezal.api.domains.privilege.command;

import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.privilege.command.param.UpdateRoleCommandParams;
import com.github.mohrezal.api.domains.privilege.constant.Permissions;
import com.github.mohrezal.api.domains.privilege.dto.RoleSummary;
import com.github.mohrezal.api.domains.privilege.dto.UpdateRoleRequest;
import com.github.mohrezal.api.domains.privilege.exception.types.OwnerRoleCannotBeUpdatedException;
import com.github.mohrezal.api.domains.privilege.mapper.RoleMapper;
import com.github.mohrezal.api.domains.privilege.model.Permission;
import com.github.mohrezal.api.domains.privilege.model.Role;
import com.github.mohrezal.api.domains.privilege.model.RolePermission;
import com.github.mohrezal.api.domains.privilege.repository.PermissionRepository;
import com.github.mohrezal.api.domains.privilege.repository.RoleRepository;
import com.github.mohrezal.api.domains.privilege.repository.UserRoleRepository;
import com.github.mohrezal.api.domains.privilege.service.UserPrivilegeVersionService;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.config.ApplicationProperties;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateRoleCommandTest {

    private static final String OWNER_ROLE_KEY = "owner";
    private static final String ROLE_KEY = "support";
    private static final ApplicationProperties.Privilege PRIVILEGE_PROPERTIES =
            new ApplicationProperties.Privilege(
                    new ApplicationProperties.Privilege.Role(
                            new ApplicationProperties.Privilege.Properties(OWNER_ROLE_KEY, "Owner"),
                            new ApplicationProperties.Privilege.Properties("user", "User")));

    @Mock private ApplicationProperties applicationProperties;

    @Mock private RoleRepository roleRepository;

    @Mock private PermissionRepository permissionRepository;

    @Mock private UserRoleRepository userRoleRepository;

    @Mock private UserRepository userRepository;

    @Mock private RoleMapper roleMapper;

    @Mock private UserPrivilegeVersionService userPrivilegeVersionService;

    @InjectMocks private UpdateRoleCommand command;

    @Test
    void execute_whenPermissionsChange_reconcilesAssignmentsWithoutChangingKey() {
        var roleId = UUID.randomUUID();
        var retainedPermission =
                Permission.builder()
                        .id(UUID.randomUUID())
                        .key(Permissions.BLOG_POSTS_CREATE)
                        .build();
        var stalePermission =
                Permission.builder()
                        .id(UUID.randomUUID())
                        .key(Permissions.BLOG_POSTS_UPDATE)
                        .build();
        var newPermission =
                Permission.builder()
                        .id(UUID.randomUUID())
                        .key(Permissions.BLOG_POSTS_DELETE)
                        .build();
        var role = Role.builder().id(roleId).key(ROLE_KEY).name("Old name").enabled(true).build();
        var retainedAssignment =
                RolePermission.builder().role(role).permission(retainedPermission).build();
        var staleAssignment =
                RolePermission.builder().role(role).permission(stalePermission).build();
        role.getPermissions().add(retainedAssignment);
        role.getPermissions().add(staleAssignment);
        var request =
                new UpdateRoleRequest(
                        "Support operators",
                        false,
                        Set.of(retainedPermission.getId(), newPermission.getId()));
        var params = new UpdateRoleCommandParams(roleId, request);
        var summary = new RoleSummary(roleId, ROLE_KEY, "Support operators", false, List.of());
        when(applicationProperties.privilege()).thenReturn(PRIVILEGE_PROPERTIES);
        when(roleRepository.findByIdWithPermissions(roleId)).thenReturn(Optional.of(role));
        when(permissionRepository.findAllById(request.permissionIds()))
                .thenReturn(List.of(retainedPermission, newPermission));
        when(roleRepository.saveAndFlush(role)).thenReturn(role);
        when(userRoleRepository.findUserIdsByRoleId(roleId)).thenReturn(List.of());
        when(roleMapper.toSummary(role)).thenReturn(summary);

        var result = command.execute(params);

        assertThat(result).isSameAs(summary);
        assertThat(role.getKey()).isEqualTo(ROLE_KEY);
        assertThat(role.getName()).isEqualTo("Support operators");
        assertThat(role.getEnabled()).isFalse();
        assertThat(role.getPermissions())
                .extracting(rolePermission -> rolePermission.getPermission().getId())
                .containsExactlyInAnyOrder(retainedPermission.getId(), newPermission.getId());
        assertThat(role.getPermissions())
                .filteredOn(
                        rolePermission ->
                                rolePermission
                                        .getPermission()
                                        .getId()
                                        .equals(retainedPermission.getId()))
                .singleElement()
                .isSameAs(retainedAssignment);
        verify(roleRepository).saveAndFlush(role);
        verify(userRoleRepository).findUserIdsByRoleId(roleId);
        verifyNoInteractions(userRepository, userPrivilegeVersionService);
        verify(roleMapper).toSummary(role);
    }

    @Test
    void execute_whenRoleIsOwner_rejectsBeforeMutation() {
        var roleId = UUID.randomUUID();
        var role =
                Role.builder().id(roleId).key(OWNER_ROLE_KEY).name("Owner").enabled(true).build();
        var request = new UpdateRoleRequest("Changed owner", false, Set.of());
        var params = new UpdateRoleCommandParams(roleId, request);
        when(applicationProperties.privilege()).thenReturn(PRIVILEGE_PROPERTIES);
        when(roleRepository.findByIdWithPermissions(roleId)).thenReturn(Optional.of(role));

        assertThatThrownBy(() -> command.execute(params))
                .isInstanceOf(OwnerRoleCannotBeUpdatedException.class);
        assertThat(role.getName()).isEqualTo("Owner");
        assertThat(role.getEnabled()).isTrue();
        verify(roleRepository, never()).saveAndFlush(any());
        verifyNoInteractions(
                permissionRepository,
                userRoleRepository,
                userRepository,
                roleMapper,
                userPrivilegeVersionService);
    }

    @Test
    void execute_whenRoleHasAssignedUsers_incrementsPrivilegeVersion() {
        var roleId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var permission =
                Permission.builder()
                        .id(UUID.randomUUID())
                        .key(Permissions.BLOG_POSTS_CREATE)
                        .build();
        var role = Role.builder().id(roleId).key(ROLE_KEY).name("Old name").enabled(true).build();
        var request = new UpdateRoleRequest("Support operators", true, Set.of(permission.getId()));
        var params = new UpdateRoleCommandParams(roleId, request);
        var summary = new RoleSummary(roleId, ROLE_KEY, "Support operators", true, List.of());
        var user = aUser().withId(userId).build();
        when(applicationProperties.privilege()).thenReturn(PRIVILEGE_PROPERTIES);
        when(roleRepository.findByIdWithPermissions(roleId)).thenReturn(Optional.of(role));
        when(permissionRepository.findAllById(request.permissionIds()))
                .thenReturn(List.of(permission));
        when(roleRepository.saveAndFlush(role)).thenReturn(role);
        when(userRoleRepository.findUserIdsByRoleId(roleId)).thenReturn(List.of(userId));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleMapper.toSummary(role)).thenReturn(summary);

        var result = command.execute(params);

        assertThat(result).isSameAs(summary);
        verify(userPrivilegeVersionService).increment(user);
    }
}

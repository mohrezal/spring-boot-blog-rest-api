package com.github.mohrezal.api.domains.privilege.command;

import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.privilege.command.param.UpdatePermissionCommandParams;
import com.github.mohrezal.api.domains.privilege.constant.Permissions;
import com.github.mohrezal.api.domains.privilege.dto.PermissionSummary;
import com.github.mohrezal.api.domains.privilege.dto.UpdatePermissionRequest;
import com.github.mohrezal.api.domains.privilege.exception.types.ProtectedPermissionCannotBeDisabledException;
import com.github.mohrezal.api.domains.privilege.mapper.PermissionMapper;
import com.github.mohrezal.api.domains.privilege.model.Permission;
import com.github.mohrezal.api.domains.privilege.repository.PermissionRepository;
import com.github.mohrezal.api.domains.privilege.repository.UserRoleRepository;
import com.github.mohrezal.api.domains.privilege.service.UserPrivilegeVersionService;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdatePermissionCommandTest {

    @Mock private PermissionRepository permissionRepository;

    @Mock private PermissionMapper permissionMapper;

    @Mock private UserRoleRepository userRoleRepository;

    @Mock private UserRepository userRepository;

    @Mock private UserPrivilegeVersionService userPrivilegeVersionService;

    @InjectMocks private UpdatePermissionCommand command;

    @Test
    void execute_whenDisablingProtectedPermission_rejectsBeforeMutation() {
        var permissionId = UUID.randomUUID();
        var permission =
                Permission.builder()
                        .id(permissionId)
                        .key(Permissions.BLOG_PRIVILEGE_PERMISSIONS_UPDATE)
                        .name("Update privilege permissions")
                        .service("blog")
                        .enabled(true)
                        .build();
        var request = new UpdatePermissionRequest("Changed name", false);
        var params = new UpdatePermissionCommandParams(permissionId, request);
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(permission));

        assertThatThrownBy(() -> command.execute(params))
                .isInstanceOf(ProtectedPermissionCannotBeDisabledException.class);
        assertThat(permission.getName()).isEqualTo("Update privilege permissions");
        assertThat(permission.getEnabled()).isTrue();
        verify(permissionRepository, never()).save(any());
        verifyNoInteractions(
                permissionMapper, userRoleRepository, userRepository, userPrivilegeVersionService);
    }

    @Test
    void execute_whenEnabledChanges_incrementsPrivilegeVersion() {
        var permissionId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var permission =
                Permission.builder()
                        .id(permissionId)
                        .key(Permissions.BLOG_POSTS_CREATE)
                        .name("Create posts")
                        .service("blog")
                        .enabled(true)
                        .build();
        var request = new UpdatePermissionRequest("Create blog posts", false);
        var params = new UpdatePermissionCommandParams(permissionId, request);
        var summary =
                new PermissionSummary(
                        permissionId,
                        Permissions.BLOG_POSTS_CREATE,
                        "Create blog posts",
                        "blog",
                        false);
        var user = aUser().withId(userId).build();
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(permission));
        when(permissionRepository.save(permission)).thenReturn(permission);
        when(userRoleRepository.findUserIdsByPermissionId(permissionId))
                .thenReturn(List.of(userId));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(permissionMapper.toSummary(permission)).thenReturn(summary);

        var result = command.execute(params);

        assertThat(result).isSameAs(summary);
        assertThat(permission.getName()).isEqualTo("Create blog posts");
        assertThat(permission.getEnabled()).isFalse();
        verify(userPrivilegeVersionService).increment(user);
    }

    @Test
    void execute_whenEnabledUnchanged_doesNotIncrementPrivilegeVersion() {
        var permissionId = UUID.randomUUID();
        var permission =
                Permission.builder()
                        .id(permissionId)
                        .key(Permissions.BLOG_POSTS_CREATE)
                        .name("Create posts")
                        .service("blog")
                        .enabled(true)
                        .build();
        var request = new UpdatePermissionRequest("Create blog posts", true);
        var params = new UpdatePermissionCommandParams(permissionId, request);
        var summary =
                new PermissionSummary(
                        permissionId,
                        Permissions.BLOG_POSTS_CREATE,
                        "Create blog posts",
                        "blog",
                        true);
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(permission));
        when(permissionRepository.save(permission)).thenReturn(permission);
        when(permissionMapper.toSummary(permission)).thenReturn(summary);

        var result = command.execute(params);

        assertThat(result).isSameAs(summary);
        assertThat(permission.getName()).isEqualTo("Create blog posts");
        assertThat(permission.getEnabled()).isTrue();
        verifyNoInteractions(userRoleRepository, userRepository, userPrivilegeVersionService);
    }
}

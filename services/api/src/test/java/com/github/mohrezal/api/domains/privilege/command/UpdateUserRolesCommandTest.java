package com.github.mohrezal.api.domains.privilege.command;

import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.privilege.command.param.UpdateUserRolesCommandParams;
import com.github.mohrezal.api.domains.privilege.dto.RoleSummary;
import com.github.mohrezal.api.domains.privilege.dto.UpdateUserRolesRequest;
import com.github.mohrezal.api.domains.privilege.exception.types.LastOwnerRoleCannotBeRemovedException;
import com.github.mohrezal.api.domains.privilege.mapper.RoleMapper;
import com.github.mohrezal.api.domains.privilege.model.Role;
import com.github.mohrezal.api.domains.privilege.model.UserRole;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateUserRolesCommandTest {

    private static final String OWNER_ROLE_KEY = "owner";
    private static final ApplicationProperties.Privilege PRIVILEGE_PROPERTIES =
            new ApplicationProperties.Privilege(
                    new ApplicationProperties.Privilege.Role(
                            new ApplicationProperties.Privilege.Properties(OWNER_ROLE_KEY, "Owner"),
                            new ApplicationProperties.Privilege.Properties("user", "User")));

    @Mock private ApplicationProperties applicationProperties;

    @Mock private UserRepository userRepository;

    @Mock private RoleRepository roleRepository;

    @Mock private UserRoleRepository userRoleRepository;

    @Mock private RoleMapper roleMapper;

    @Mock private UserPrivilegeVersionService userPrivilegeVersionService;

    @InjectMocks private UpdateUserRolesCommand command;

    @Test
    void execute_whenRemovingLastOwnerRole_rejectsBeforeMutation() {
        var userId = UUID.randomUUID();
        var ownerRoleId = UUID.randomUUID();
        var user = aUser().withId(userId).build();
        var ownerRole = Role.builder().id(ownerRoleId).key(OWNER_ROLE_KEY).name("Owner").build();
        var params = new UpdateUserRolesCommandParams(userId, new UpdateUserRolesRequest(Set.of()));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(applicationProperties.privilege()).thenReturn(PRIVILEGE_PROPERTIES);
        when(roleRepository.findByKeyForUpdate(OWNER_ROLE_KEY)).thenReturn(Optional.of(ownerRole));
        when(userRoleRepository.existsByUserAndRole(user, ownerRole)).thenReturn(true);
        when(userRoleRepository.countByRole(ownerRole)).thenReturn(1L);

        assertThatThrownBy(() -> command.execute(params))
                .isInstanceOf(LastOwnerRoleCannotBeRemovedException.class);
        verify(roleRepository).findByKeyForUpdate(OWNER_ROLE_KEY);
        verify(userRoleRepository, never()).findAllByUser(any());
        verify(userRoleRepository, never()).deleteAll(any());
        verify(userRoleRepository, never()).saveAll(any());
        verifyNoInteractions(roleMapper, userPrivilegeVersionService);
    }

    @Test
    void execute_whenAssignmentsChange_incrementsPrivilegeVersion() {
        var userId = UUID.randomUUID();
        var ownerRoleId = UUID.randomUUID();
        var supportRoleId = UUID.randomUUID();
        var user = aUser().withId(userId).build();
        var ownerRole = Role.builder().id(ownerRoleId).key(OWNER_ROLE_KEY).name("Owner").build();
        var supportRole = Role.builder().id(supportRoleId).key("support").name("Support").build();
        var existingAssignment = UserRole.builder().user(user).role(ownerRole).build();
        var summary = new RoleSummary(supportRoleId, "support", "Support", true, List.of());
        var params =
                new UpdateUserRolesCommandParams(
                        userId, new UpdateUserRolesRequest(Set.of(supportRoleId)));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findAllByIdWithPermissions(Set.of(supportRoleId)))
                .thenReturn(List.of(supportRole));
        when(applicationProperties.privilege()).thenReturn(PRIVILEGE_PROPERTIES);
        when(roleRepository.findByKeyForUpdate(OWNER_ROLE_KEY)).thenReturn(Optional.of(ownerRole));
        when(userRoleRepository.existsByUserAndRole(user, ownerRole)).thenReturn(true);
        when(userRoleRepository.countByRole(ownerRole)).thenReturn(2L);
        when(userRoleRepository.findAllByUser(user)).thenReturn(List.of(existingAssignment));
        when(roleMapper.toSummary(supportRole)).thenReturn(summary);

        var result = command.execute(params);

        assertThat(result).containsExactly(summary);
        verify(userRoleRepository).deleteAll(List.of(existingAssignment));
        ArgumentCaptor<List<UserRole>> createdCaptor = ArgumentCaptor.captor();
        verify(userRoleRepository).saveAll(createdCaptor.capture());
        assertThat(createdCaptor.getValue())
                .singleElement()
                .satisfies(
                        userRole -> {
                            assertThat(userRole.getUser()).isSameAs(user);
                            assertThat(userRole.getRole()).isSameAs(supportRole);
                        });
        verify(userPrivilegeVersionService).increment(user);
    }

    @Test
    void execute_whenAssignmentsUnchanged_doesNotIncrementPrivilegeVersion() {
        var userId = UUID.randomUUID();
        var ownerRoleId = UUID.randomUUID();
        var user = aUser().withId(userId).build();
        var ownerRole = Role.builder().id(ownerRoleId).key(OWNER_ROLE_KEY).name("Owner").build();
        var existingAssignment = UserRole.builder().user(user).role(ownerRole).build();
        var summary = new RoleSummary(ownerRoleId, OWNER_ROLE_KEY, "Owner", true, List.of());
        var params =
                new UpdateUserRolesCommandParams(
                        userId, new UpdateUserRolesRequest(Set.of(ownerRoleId)));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findAllByIdWithPermissions(Set.of(ownerRoleId)))
                .thenReturn(List.of(ownerRole));
        when(applicationProperties.privilege()).thenReturn(PRIVILEGE_PROPERTIES);
        when(roleRepository.findByKeyForUpdate(OWNER_ROLE_KEY)).thenReturn(Optional.of(ownerRole));
        when(userRoleRepository.findAllByUser(user)).thenReturn(List.of(existingAssignment));
        when(roleMapper.toSummary(ownerRole)).thenReturn(summary);

        var result = command.execute(params);

        assertThat(result).containsExactly(summary);
        verify(userRoleRepository).deleteAll(List.of());
        verify(userRoleRepository).saveAll(List.of());
        verify(userPrivilegeVersionService, never()).increment(any());
    }
}

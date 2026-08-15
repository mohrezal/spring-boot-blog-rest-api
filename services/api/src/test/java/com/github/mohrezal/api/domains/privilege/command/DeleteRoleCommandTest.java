package com.github.mohrezal.api.domains.privilege.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.privilege.command.param.DeleteRoleCommandParams;
import com.github.mohrezal.api.domains.privilege.exception.types.ConfiguredRoleCannotBeDeletedException;
import com.github.mohrezal.api.domains.privilege.exception.types.RoleAssignedToUsersException;
import com.github.mohrezal.api.domains.privilege.model.Role;
import com.github.mohrezal.api.domains.privilege.repository.RoleRepository;
import com.github.mohrezal.api.domains.privilege.repository.UserRoleRepository;
import com.github.mohrezal.api.shared.config.ApplicationProperties;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteRoleCommandTest {

    private static final String OWNER_ROLE_KEY = "owner";
    private static final String USER_ROLE_KEY = "user";
    private static final ApplicationProperties.Privilege PRIVILEGE_PROPERTIES =
            new ApplicationProperties.Privilege(
                    new ApplicationProperties.Privilege.Role(
                            new ApplicationProperties.Privilege.Properties(OWNER_ROLE_KEY, "Owner"),
                            new ApplicationProperties.Privilege.Properties(USER_ROLE_KEY, "User")));

    @Mock private ApplicationProperties applicationProperties;

    @Mock private RoleRepository roleRepository;

    @Mock private UserRoleRepository userRoleRepository;

    @InjectMocks private DeleteRoleCommand command;

    @Test
    void execute_whenRoleIsConfiguredOwner_rejectsBeforeDelete() {
        var roleId = UUID.randomUUID();
        var role = Role.builder().id(roleId).key(OWNER_ROLE_KEY).name("Owner").build();
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(applicationProperties.privilege()).thenReturn(PRIVILEGE_PROPERTIES);

        assertThatThrownBy(() -> command.execute(new DeleteRoleCommandParams(roleId)))
                .isInstanceOf(ConfiguredRoleCannotBeDeletedException.class);
        verify(roleRepository, never()).delete(any());
        verifyNoInteractions(userRoleRepository);
    }

    @Test
    void execute_whenRoleIsConfiguredUser_rejectsBeforeDelete() {
        var roleId = UUID.randomUUID();
        var role = Role.builder().id(roleId).key(USER_ROLE_KEY).name("User").build();
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(applicationProperties.privilege()).thenReturn(PRIVILEGE_PROPERTIES);

        assertThatThrownBy(() -> command.execute(new DeleteRoleCommandParams(roleId)))
                .isInstanceOf(ConfiguredRoleCannotBeDeletedException.class);
        verify(roleRepository, never()).delete(any());
        verifyNoInteractions(userRoleRepository);
    }

    @Test
    void execute_whenRoleAssignedToUsers_rejectsBeforeDelete() {
        var roleId = UUID.randomUUID();
        var role = Role.builder().id(roleId).key("support").name("Support").build();
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(applicationProperties.privilege()).thenReturn(PRIVILEGE_PROPERTIES);
        when(userRoleRepository.existsByRole(role)).thenReturn(true);

        assertThatThrownBy(() -> command.execute(new DeleteRoleCommandParams(roleId)))
                .isInstanceOf(RoleAssignedToUsersException.class);
        verify(roleRepository, never()).delete(any());
    }

    @Test
    void execute_whenRoleUnassigned_deletesRole() {
        var roleId = UUID.randomUUID();
        var role = Role.builder().id(roleId).key("support").name("Support").build();
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(applicationProperties.privilege()).thenReturn(PRIVILEGE_PROPERTIES);
        when(userRoleRepository.existsByRole(role)).thenReturn(false);

        command.execute(new DeleteRoleCommandParams(roleId));

        verify(roleRepository).delete(role);
    }
}

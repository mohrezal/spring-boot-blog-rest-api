package com.github.mohrezal.api.domains.privilege.service;

import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.privilege.model.Role;
import com.github.mohrezal.api.domains.privilege.model.UserRole;
import com.github.mohrezal.api.domains.privilege.repository.RoleRepository;
import com.github.mohrezal.api.domains.privilege.repository.UserRoleRepository;
import com.github.mohrezal.api.shared.config.ApplicationProperties;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserRoleAssignmentServiceTest {

    private static final String USER_ROLE_KEY = "user";

    @Mock private RoleRepository roleRepository;

    @Mock private UserRoleRepository userRoleRepository;

    private UserRoleAssignmentService service;

    @BeforeEach
    void setUp() {
        var applicationProperties =
                new ApplicationProperties(
                        null,
                        null,
                        null,
                        null,
                        null,
                        new ApplicationProperties.Owner("owner@example.com"),
                        new ApplicationProperties.Privilege(
                                new ApplicationProperties.Privilege.Role(
                                        new ApplicationProperties.Privilege.Properties(
                                                "owner", "Owner"),
                                        new ApplicationProperties.Privilege.Properties(
                                                USER_ROLE_KEY, "User"))));
        service =
                new UserRoleAssignmentService(
                        applicationProperties, roleRepository, userRoleRepository);
    }

    @Test
    void assignConfiguredUserRole_whenRoleMissing_shouldThrowIllegalStateException() {
        var user = aUser().build();
        when(roleRepository.findByKey(USER_ROLE_KEY)).thenReturn(Optional.empty());

        var exception =
                assertThrows(
                        IllegalStateException.class, () -> service.assignConfiguredUserRole(user));

        assertEquals("Configured user role not found: " + USER_ROLE_KEY, exception.getMessage());
        verify(userRoleRepository, never()).save(any());
    }

    @Test
    void assignConfiguredUserRole_whenAlreadyAssigned_shouldNotCreateDuplicate() {
        var user = aUser().build();
        var role = Role.builder().key(USER_ROLE_KEY).name("User").build();
        when(roleRepository.findByKey(USER_ROLE_KEY)).thenReturn(Optional.of(role));
        when(userRoleRepository.existsByUserAndRole(user, role)).thenReturn(true);

        service.assignConfiguredUserRole(user);

        verify(userRoleRepository, never()).save(any());
    }

    @Test
    void assignConfiguredUserRole_whenNotAssigned_shouldSaveUserRole() {
        var user = aUser().build();
        var role = Role.builder().key(USER_ROLE_KEY).name("User").build();
        when(roleRepository.findByKey(USER_ROLE_KEY)).thenReturn(Optional.of(role));
        when(userRoleRepository.existsByUserAndRole(user, role)).thenReturn(false);

        service.assignConfiguredUserRole(user);

        var captor = ArgumentCaptor.forClass(UserRole.class);
        verify(userRoleRepository).save(captor.capture());
        assertEquals(user, captor.getValue().getUser());
        assertEquals(role, captor.getValue().getRole());
    }
}

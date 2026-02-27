package com.github.mohrezal.api.domains.users.commands;

import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.notifications.events.UserRegisteredEvent;
import com.github.mohrezal.api.domains.notifications.models.NotificationPreference;
import com.github.mohrezal.api.domains.notifications.repositories.NotificationPreferenceRepository;
import com.github.mohrezal.api.domains.users.commands.params.RegisterUserCommandParams;
import com.github.mohrezal.api.domains.users.dtos.RegisterUserRequest;
import com.github.mohrezal.api.domains.users.dtos.UserSummary;
import com.github.mohrezal.api.domains.users.enums.UserRole;
import com.github.mohrezal.api.domains.users.exceptions.types.UserHandleReservedException;
import com.github.mohrezal.api.domains.users.mappers.UserMapper;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.domains.users.services.registration.RegistrationService;
import com.github.mohrezal.api.shared.config.ApplicationProperties;
import com.github.mohrezal.api.shared.services.deviceinfo.RequestInfoService;
import com.github.mohrezal.api.shared.services.jwt.JwtService;
import com.github.mohrezal.api.support.constants.UserAgents;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class RegisterUserCommandTest {
    @Mock private RegistrationService registrationService;

    @Mock private JwtService jwtService;

    @Mock private UserMapper userMapper;

    @Mock private RequestInfoService deviceInfoService;

    @Mock private UserRepository userRepository;

    @Mock private NotificationPreferenceRepository notificationPreferenceRepository;

    @Mock private ApplicationEventPublisher eventPublisher;

    private final User user = aUser().build();

    private final RegisterUserRequest request =
            new RegisterUserRequest(
                    "John",
                    "Doe",
                    "johndoe@gmail.com",
                    "john_doe",
                    "Password!123",
                    "Hey, I'm John Deo.");

    private final RegisterUserCommandParams params =
            new RegisterUserCommandParams(request, "127.0.0.1", UserAgents.MAC);

    private RegisterUserCommand command;

    private RegisterUserCommand createCommand(List<String> reservedHandles) {
        ApplicationProperties applicationProperties =
                new ApplicationProperties(
                        null, null, null, new ApplicationProperties.Handle(reservedHandles), null);
        return new RegisterUserCommand(
                registrationService,
                jwtService,
                userMapper,
                deviceInfoService,
                userRepository,
                applicationProperties,
                notificationPreferenceRepository,
                eventPublisher);
    }

    @BeforeEach
    void setUp() {
        command = createCommand(List.of("admin", "owner"));
    }

    @Test
    void execute_whenValidRequest_shouldRegisterUserAndReturnRegisterResponse() {
        var userSummary =
                new UserSummary(
                        UUID.randomUUID(),
                        "johndoe@gmail.com",
                        "john_doe",
                        "John",
                        "Doe",
                        "Hey, I'm John Deo.",
                        null,
                        UserRole.USER,
                        false,
                        null,
                        null);

        when(userRepository.existsByHandle(eq(params.registerUserRequest().handle())))
                .thenReturn(false);
        when(registrationService.register(eq(params.registerUserRequest()), eq(UserRole.USER)))
                .thenReturn(user);
        when(jwtService.generateAccessToken(eq(user))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(eq(user.getId()))).thenReturn("refresh-token");
        when(deviceInfoService.parseDeviceName(eq(UserAgents.MAC))).thenReturn("Mac OS");
        when(userMapper.toUserSummary(user)).thenReturn(userSummary);

        var result = command.execute(params);

        assertNotNull(result);
        assertNotNull(result.user());
        assertNotNull(result.authResponse());

        assertEquals("access-token", result.authResponse().accessToken());
        assertEquals("refresh-token", result.authResponse().refreshToken());

        verify(notificationPreferenceRepository).save(any(NotificationPreference.class));

        verify(jwtService)
                .saveRefreshToken(
                        eq("refresh-token"),
                        eq(user),
                        eq("127.0.0.1"),
                        eq(UserAgents.MAC),
                        eq("Mac OS"));

        verify(eventPublisher).publishEvent(any(UserRegisteredEvent.class));
    }

    @Test
    void execute_whenHandleIsReserved_shouldThrowUserHandleReservedException() {
        command = createCommand(List.of("john_doe"));

        assertThrows(UserHandleReservedException.class, () -> command.execute(params));

        verifyNoInteractions(registrationService, jwtService, eventPublisher);
    }

    @Test
    void execute_whenUnexpectedExceptionOccurs_shouldRethrow() {
        command = createCommand(List.of());

        when(userRepository.existsByHandle("john_doe")).thenReturn(false);

        when(registrationService.register(any(), any())).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> command.execute(params));
    }
}

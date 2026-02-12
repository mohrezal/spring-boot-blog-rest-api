package com.github.mohrezal.api.domains.users.commands;

import com.github.mohrezal.api.domains.notifications.events.UserRegisteredEvent;
import com.github.mohrezal.api.domains.notifications.models.NotificationPreference;
import com.github.mohrezal.api.domains.notifications.repositories.NotificationPreferenceRepository;
import com.github.mohrezal.api.domains.users.commands.params.RegisterUserCommandParams;
import com.github.mohrezal.api.domains.users.dtos.AuthResponse;
import com.github.mohrezal.api.domains.users.dtos.RegisterResponse;
import com.github.mohrezal.api.domains.users.enums.UserRole;
import com.github.mohrezal.api.domains.users.exceptions.types.UserHandleAlreadyExistsException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserHandleReservedException;
import com.github.mohrezal.api.domains.users.mappers.UserMapper;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.domains.users.services.registration.RegistrationService;
import com.github.mohrezal.api.shared.config.ApplicationProperties;
import com.github.mohrezal.api.shared.interfaces.Command;
import com.github.mohrezal.api.shared.services.deviceinfo.RequestInfoService;
import com.github.mohrezal.api.shared.services.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Slf4j
public class RegisterUserCommand implements Command<RegisterUserCommandParams, RegisterResponse> {

    private final RegistrationService registrationService;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final RequestInfoService deviceInfoService;
    private final UserRepository userRepository;
    private final ApplicationProperties applicationProperties;
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void validate(RegisterUserCommandParams params) {
        String handle = params.registerUserRequest().handle().toLowerCase();

        if (applicationProperties.handle().reservedHandles().contains(handle)) {
            throw new UserHandleReservedException();
        }

        if (userRepository.existsByHandle(handle)) {
            throw new UserHandleAlreadyExistsException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RegisterResponse execute(RegisterUserCommandParams params) {
        validate(params);
        try {
            var user = registrationService.register(params.registerUserRequest(), UserRole.USER);

            var notificationPreference = NotificationPreference.builder().user(user).build();
            notificationPreferenceRepository.save(notificationPreference);

            var accessToken = jwtService.generateAccessToken(user);
            var refreshToken = jwtService.generateRefreshToken(user.getId());

            var deviceName = deviceInfoService.parseDeviceName(params.userAgent());

            jwtService.saveRefreshToken(
                    refreshToken, user, params.ipAddress(), params.userAgent(), deviceName);

            eventPublisher.publishEvent(new UserRegisteredEvent(user));

            var authResponse = new AuthResponse(accessToken, refreshToken);
            log.info("User registration successful.");
            return new RegisterResponse(userMapper.toUserSummary(user), authResponse);
        } catch (UserHandleReservedException | UserHandleAlreadyExistsException ex) {
            log.warn("User registration failed - message: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error during user registration operation", ex);
            throw ex;
        }
    }
}

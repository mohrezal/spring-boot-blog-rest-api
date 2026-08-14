package com.github.mohrezal.api.domains.users.commands;

import com.github.mohrezal.api.domains.notifications.models.NotificationPreference;
import com.github.mohrezal.api.domains.notifications.repositories.NotificationPreferenceRepository;
import com.github.mohrezal.api.domains.users.commands.params.RegisterUserCommandParams;
import com.github.mohrezal.api.domains.users.dtos.AuthResponse;
import com.github.mohrezal.api.domains.users.dtos.RegisterResponse;
import com.github.mohrezal.api.domains.users.exceptions.context.UserRegisterExceptionContext;
import com.github.mohrezal.api.domains.users.exceptions.types.UserEmailAlreadyExistsException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserHandleAlreadyExistsException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserHandleReservedException;
import com.github.mohrezal.api.domains.users.mappers.UserMapper;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.domains.users.services.registration.RegistrationService;
import com.github.mohrezal.api.shared.config.ApplicationProperties;
import com.github.mohrezal.api.shared.exceptions.types.InvalidRedirectUrlException;
import com.github.mohrezal.api.shared.interfaces.Command;
import com.github.mohrezal.api.shared.services.deviceinfo.RequestInfoService;
import com.github.mohrezal.api.shared.services.hash.HashService;
import com.github.mohrezal.api.shared.services.jwt.JwtService;
import com.github.mohrezal.api.shared.utils.RedirectUrlUtils;
import com.github.mohrezal.common.redis.RedisCacheService;
import com.github.mohrezal.common.redis.constants.RedisKeyFactory;
import com.github.mohrezal.common.worker.events.UserRegisteredEvent;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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
    private final RedisCacheService redisCacheService;
    private final HashService hashService;
    private final RedirectUrlUtils redirectUrlUtils;

    @Override
    public void validate(RegisterUserCommandParams params) {
        if (!redirectUrlUtils.isValid(params.redirectUrl())) {
            throw new InvalidRedirectUrlException();
        }

        var handle = params.registerUserRequest().handle().toLowerCase();
        var request = params.registerUserRequest();
        var context = new UserRegisterExceptionContext(request.email(), request.handle());

        if (applicationProperties.handle().reservedHandles().contains(handle)) {
            throw new UserHandleReservedException(context);
        }

        if (userRepository.existsByHandle(handle)) {
            throw new UserHandleAlreadyExistsException(context);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RegisterResponse execute(RegisterUserCommandParams params) {
        validate(params);
        try {
            var user = registrationService.register(params.registerUserRequest());

            var notificationPreference = NotificationPreference.builder().user(user).build();
            notificationPreferenceRepository.save(notificationPreference);

            var accessToken = jwtService.generateAccessToken(user);
            var refreshToken = jwtService.generateRefreshToken(user.getId());

            var deviceName = deviceInfoService.parseDeviceName(params.userAgent());

            jwtService.saveRefreshToken(
                    refreshToken, user, params.ipAddress(), params.userAgent(), deviceName);

            var token = hashService.sha256(UUID.randomUUID().toString());

            eventPublisher.publishEvent(
                    new UserRegisteredEvent(
                            user.getId(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getEmail(),
                            token,
                            params.redirectUrl()));

            redisCacheService.set(
                    RedisKeyFactory.Verification.token(token),
                    user.getId(),
                    Duration.ofSeconds(RedisKeyFactory.Verification.TTL_SECONDS));

            var authResponse = new AuthResponse(accessToken, refreshToken);

            log.info("User registration successful.");
            return new RegisterResponse(userMapper.toUserSummary(user), authResponse);
        } catch (UserEmailAlreadyExistsException ex) {
            var request = params.registerUserRequest();
            var context = new UserRegisterExceptionContext(request.email(), request.handle());
            throw new UserEmailAlreadyExistsException(context, ex);
        }
    }
}

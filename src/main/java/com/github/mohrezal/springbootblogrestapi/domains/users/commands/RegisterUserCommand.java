package com.github.mohrezal.springbootblogrestapi.domains.users.commands;

import com.github.mohrezal.springbootblogrestapi.domains.users.commands.params.RegisterUserCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.AuthResponse;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.RegisterResponse;
import com.github.mohrezal.springbootblogrestapi.domains.users.enums.UserRole;
import com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types.UserHandleAlreadyExistsException;
import com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types.UserHandleReservedException;
import com.github.mohrezal.springbootblogrestapi.domains.users.mappers.UserMapper;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.domains.users.repositories.UserRepository;
import com.github.mohrezal.springbootblogrestapi.domains.users.services.registration.RegistrationService;
import com.github.mohrezal.springbootblogrestapi.shared.config.ApplicationProperties;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Command;
import com.github.mohrezal.springbootblogrestapi.shared.services.deviceinfo.RequestInfoService;
import com.github.mohrezal.springbootblogrestapi.shared.services.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RegisterResponse execute(RegisterUserCommandParams params) {
        User user = registrationService.register(params.getRegisterUserRequest(), UserRole.USER);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        String deviceName = deviceInfoService.parseDeviceName(params.getUserAgent());

        jwtService.saveRefreshToken(
                refreshToken, user, params.getIpAddress(), params.getUserAgent(), deviceName);

        AuthResponse authResponse =
                AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();

        return RegisterResponse.builder()
                .user(userMapper.toUserSummary(user))
                .authResponse(authResponse)
                .build();
    }

    @Override
    public void validate(RegisterUserCommandParams params) {
        String handle = params.getRegisterUserRequest().getHandle().toLowerCase();

        if (applicationProperties.handle().reservedHandles().contains(handle)) {
            throw new UserHandleReservedException();
        }

        if (userRepository.existsByHandle(handle)) {
            throw new UserHandleAlreadyExistsException();
        }
    }
}

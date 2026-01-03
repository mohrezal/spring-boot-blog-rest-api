package com.github.mohrezal.springbootblogrestapi.domains.users.commands;

import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.AuthResponse;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.RegisterResponse;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.RegisterUserRequest;
import com.github.mohrezal.springbootblogrestapi.domains.users.enums.UserRole;
import com.github.mohrezal.springbootblogrestapi.domains.users.mappers.UserMapper;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.domains.users.services.registration.RegistrationService;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Command;
import com.github.mohrezal.springbootblogrestapi.shared.services.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterUserCommand implements Command<RegisterUserRequest, RegisterResponse> {

    private final RegistrationService registrationService;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Override
    public RegisterResponse execute(RegisterUserRequest params) {
        User user = registrationService.register(params, UserRole.USER);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        AuthResponse authResponse =
                AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();

        return RegisterResponse.builder()
                .user(userMapper.toUserSummary(user))
                .authResponse(authResponse)
                .build();
    }
}

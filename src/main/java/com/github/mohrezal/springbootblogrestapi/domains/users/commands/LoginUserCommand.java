package com.github.mohrezal.springbootblogrestapi.domains.users.commands;

import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.AuthResponse;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.LoginRequest;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.domains.users.services.authentication.AuthenticationService;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Command;
import com.github.mohrezal.springbootblogrestapi.shared.services.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUserCommand implements Command<LoginRequest, AuthResponse> {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @Override
    public AuthResponse execute(LoginRequest params) {
        User user = authenticationService.authenticate(params);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }
}

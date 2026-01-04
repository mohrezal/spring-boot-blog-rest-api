package com.github.mohrezal.springbootblogrestapi.domains.users.commands;

import com.github.mohrezal.springbootblogrestapi.domains.users.commands.params.LoginUserCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.AuthResponse;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.domains.users.services.authentication.AuthenticationService;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Command;
import com.github.mohrezal.springbootblogrestapi.shared.services.deviceinfo.DeviceInfoService;
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
public class LoginUserCommand implements Command<LoginUserCommandParams, AuthResponse> {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final DeviceInfoService deviceInfoService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AuthResponse execute(LoginUserCommandParams params) {
        User user = authenticationService.authenticate(params.getLoginRequest());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        String deviceName = deviceInfoService.parseDeviceName(params.getUserAgent());

        jwtService.saveRefreshToken(
                refreshToken, user, params.getIpAddress(), params.getUserAgent(), deviceName);

        return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }
}

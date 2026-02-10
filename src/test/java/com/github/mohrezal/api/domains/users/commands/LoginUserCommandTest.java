package com.github.mohrezal.api.domains.users.commands;

import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.users.commands.params.LoginUserCommandParams;
import com.github.mohrezal.api.domains.users.dtos.LoginRequest;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.services.authentication.AuthenticationService;
import com.github.mohrezal.api.shared.services.deviceinfo.RequestInfoService;
import com.github.mohrezal.api.shared.services.jwt.JwtService;
import com.github.mohrezal.api.support.constants.UserAgents;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginUserCommandTest {

    @Mock private AuthenticationService authenticationService;

    @Mock private JwtService jwtService;

    @Mock private RequestInfoService deviceInfoService;

    @InjectMocks private LoginUserCommand command;

    private final User mockedUser = aUser().build();
    private final LoginRequest request = new LoginRequest(mockedUser.getEmail(), "Password!123456");
    private final LoginUserCommandParams params =
            new LoginUserCommandParams(request, "127.0.0.1", UserAgents.MAC);

    @Test
    void execute_whenValidCredentials_shouldReturnAuthResponse() {

        when(authenticationService.authenticate(any(LoginRequest.class))).thenReturn(mockedUser);
        when(jwtService.generateAccessToken(eq(mockedUser))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(eq(mockedUser.getId()))).thenReturn("refresh-token");
        when(deviceInfoService.parseDeviceName(anyString())).thenReturn(UserAgents.MAC);

        var result = command.execute(params);

        assertNotNull(result);
        assertEquals("access-token", result.accessToken());
        assertEquals("refresh-token", result.refreshToken());

        verify(authenticationService).authenticate(any(LoginRequest.class));
        verify(jwtService).generateAccessToken(eq(mockedUser));
        verify(jwtService).generateRefreshToken(eq(mockedUser.getId()));
        verify(deviceInfoService).parseDeviceName(eq(UserAgents.MAC));
        verify(jwtService)
                .saveRefreshToken(
                        eq("refresh-token"), eq(mockedUser), anyString(), anyString(), anyString());
    }

    @Test
    void execute_whenAuthenticationFails_shouldPropagateException() {
        when(authenticationService.authenticate(any())).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> command.execute(params));

        verify(authenticationService).authenticate(any());
        verifyNoInteractions(jwtService, deviceInfoService);
    }

    @Test
    void execute_whenAccessTokenGenerationFails_shouldPropagateException() {
        when(authenticationService.authenticate(any())).thenReturn(mockedUser);
        when(jwtService.generateAccessToken(eq(mockedUser))).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> command.execute(params));

        verify(jwtService).generateAccessToken(eq(mockedUser));
        verify(jwtService, never()).saveRefreshToken(any(), any(), any(), any(), any());
    }
}

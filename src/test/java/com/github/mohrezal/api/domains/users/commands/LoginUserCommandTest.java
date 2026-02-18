package com.github.mohrezal.api.domains.users.commands;

import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.posts.repositories.PostViewRepository;
import com.github.mohrezal.api.domains.users.commands.params.LoginUserCommandParams;
import com.github.mohrezal.api.domains.users.dtos.LoginRequest;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.services.authentication.AuthenticationService;
import com.github.mohrezal.api.shared.services.deviceinfo.RequestInfoService;
import com.github.mohrezal.api.shared.services.hash.HashService;
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

    @Mock private HashService hashService;

    @Mock private PostViewRepository postViewRepository;

    @InjectMocks private LoginUserCommand command;

    private final User mockedUser = aUser().build();
    private final LoginRequest request = new LoginRequest(mockedUser.getEmail(), "Password!123456");
    private final LoginUserCommandParams params =
            new LoginUserCommandParams(request, "127.0.0.1", UserAgents.MAC, "vid-1");

    @Test
    void execute_whenValidCredentials_shouldReturnAuthResponse() {

        when(authenticationService.authenticate(any(LoginRequest.class))).thenReturn(mockedUser);
        when(jwtService.generateAccessToken(eq(mockedUser))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(eq(mockedUser.getId()))).thenReturn("refresh-token");
        when(deviceInfoService.parseDeviceName(anyString())).thenReturn(UserAgents.MAC);
        when(hashService.sha256("vid-1")).thenReturn("vid-hash");

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
        verify(hashService).sha256("vid-1");
        verify(postViewRepository)
                .linkAnonymousViewsToUserByVidHash("vid-hash", mockedUser.getId());
    }

    @Test
    void execute_whenAuthenticationFails_shouldPropagateException() {
        when(authenticationService.authenticate(any())).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> command.execute(params));

        verify(authenticationService).authenticate(any());
        verifyNoInteractions(jwtService, deviceInfoService, hashService, postViewRepository);
    }

    @Test
    void execute_whenAccessTokenGenerationFails_shouldPropagateException() {
        when(authenticationService.authenticate(any())).thenReturn(mockedUser);
        when(jwtService.generateAccessToken(eq(mockedUser))).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> command.execute(params));

        verify(jwtService).generateAccessToken(eq(mockedUser));
        verify(jwtService, never()).saveRefreshToken(any(), any(), any(), any(), any());
        verifyNoInteractions(hashService, postViewRepository);
    }

    @Test
    void execute_whenVidIsMissing_shouldSkipAnonymousViewLinking() {
        var paramsWithoutVid =
                new LoginUserCommandParams(request, "127.0.0.1", UserAgents.MAC, null);

        when(authenticationService.authenticate(any(LoginRequest.class))).thenReturn(mockedUser);
        when(jwtService.generateAccessToken(eq(mockedUser))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(eq(mockedUser.getId()))).thenReturn("refresh-token");
        when(deviceInfoService.parseDeviceName(anyString())).thenReturn(UserAgents.MAC);

        var result = command.execute(paramsWithoutVid);

        assertNotNull(result);
        assertEquals("access-token", result.accessToken());
        assertEquals("refresh-token", result.refreshToken());
        verifyNoInteractions(hashService, postViewRepository);
    }

    @Test
    void execute_whenAnonymousViewLinkingFails_shouldStillReturnAuthResponse() {
        when(authenticationService.authenticate(any(LoginRequest.class))).thenReturn(mockedUser);
        when(jwtService.generateAccessToken(eq(mockedUser))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(eq(mockedUser.getId()))).thenReturn("refresh-token");
        when(deviceInfoService.parseDeviceName(anyString())).thenReturn(UserAgents.MAC);
        when(hashService.sha256("vid-1")).thenReturn("vid-hash");
        doThrow(RuntimeException.class)
                .when(postViewRepository)
                .linkAnonymousViewsToUserByVidHash("vid-hash", mockedUser.getId());

        var result = command.execute(params);

        assertNotNull(result);
        assertEquals("access-token", result.accessToken());
        assertEquals("refresh-token", result.refreshToken());
        verify(jwtService)
                .saveRefreshToken(
                        eq("refresh-token"), eq(mockedUser), anyString(), anyString(), anyString());
    }
}

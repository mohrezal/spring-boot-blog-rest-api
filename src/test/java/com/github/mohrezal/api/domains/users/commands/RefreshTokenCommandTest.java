package com.github.mohrezal.api.domains.users.commands;

import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.users.commands.params.RefreshTokenCommandParams;
import com.github.mohrezal.api.domains.users.exceptions.types.UserInvalidRefreshTokenException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserRefreshTokenNotFoundException;
import com.github.mohrezal.api.domains.users.models.RefreshToken;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.services.deviceinfo.RequestInfoService;
import com.github.mohrezal.api.shared.services.jwt.JwtService;
import com.github.mohrezal.api.support.constants.UserAgents;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshTokenCommandTest {

    @Mock private JwtService jwtService;

    @Mock private UserRepository userRepository;

    @Mock private RequestInfoService deviceInfoService;

    @InjectMocks private RefreshTokenCommand command;

    private final User user = aUser().build();

    private final RefreshTokenCommandParams params =
            new RefreshTokenCommandParams("refresh-token", "127.0.0.1", UserAgents.MAC);

    @Test
    void execute_whenValidRefreshToken_shouldReturnAuthResponse() {
        var refreshTokenEntity = mock(RefreshToken.class);
        when(refreshTokenEntity.getUser()).thenReturn(user);
        when(jwtService.getRefreshTokenEntity("refresh-token"))
                .thenReturn(Optional.of(refreshTokenEntity));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(user.getId())).thenReturn("new-refresh-token");
        when(deviceInfoService.parseDeviceName(UserAgents.MAC)).thenReturn("Mac OS");

        var result = command.execute(params);

        assertNotNull(result);
        assertEquals("new-access-token", result.accessToken());
        assertEquals("new-refresh-token", result.refreshToken());

        verify(jwtService).revokeRefreshToken("refresh-token");

        verify(jwtService)
                .saveRefreshToken(
                        eq("new-refresh-token"),
                        eq(user),
                        eq("127.0.0.1"),
                        eq(UserAgents.MAC),
                        eq("Mac OS"));
    }

    @Test
    void execute_whenRefreshTokenIsNull_shouldThrowUserInvalidRefreshTokenException() {
        var params = new RefreshTokenCommandParams(null, "127.0.0.1", UserAgents.MAC);

        assertThrows(UserInvalidRefreshTokenException.class, () -> command.execute(params));
        verifyNoInteractions(jwtService, userRepository);
    }

    @Test
    void execute_whenRefreshTokenIsBlank_shouldThrowUserInvalidRefreshTokenException() {
        var invalidParams = new RefreshTokenCommandParams("   ", "127.0.0.1", UserAgents.MAC);

        assertThrows(UserInvalidRefreshTokenException.class, () -> command.execute(invalidParams));

        verifyNoInteractions(jwtService, userRepository);
    }

    @Test
    void execute_whenRefreshTokenNotFound_shouldThrowUserRefreshTokenNotFoundException() {
        when(jwtService.getRefreshTokenEntity("refresh-token")).thenReturn(Optional.empty());

        assertThrows(UserRefreshTokenNotFoundException.class, () -> command.execute(params));

        verify(jwtService).getRefreshTokenEntity("refresh-token");
        verify(jwtService, never()).revokeRefreshToken(anyString());
    }

    @Test
    void execute_whenUserNotFound_shouldThrowUserNotFoundException() {
        var refreshTokenEntity = mock(RefreshToken.class);

        when(refreshTokenEntity.getUser()).thenReturn(user);
        when(jwtService.getRefreshTokenEntity("refresh-token"))
                .thenReturn(Optional.of(refreshTokenEntity));
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> command.execute(params));

        verify(jwtService, never()).revokeRefreshToken(anyString());
    }

    @Test
    void execute_whenUnexpectedExceptionOccurs_shouldRethrow() {
        when(jwtService.getRefreshTokenEntity("refresh-token")).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> command.execute(params));
    }
}

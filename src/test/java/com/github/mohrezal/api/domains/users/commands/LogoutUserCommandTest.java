package com.github.mohrezal.api.domains.users.commands;

import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.users.commands.params.LogoutUserCommandParams;
import com.github.mohrezal.api.domains.users.exceptions.types.UserInvalidRefreshTokenException;
import com.github.mohrezal.api.domains.users.models.RefreshToken;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.shared.exceptions.types.ForbiddenException;
import com.github.mohrezal.api.shared.services.jwt.JwtService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LogoutUserCommandTest {

    @Mock private JwtService jwtService;

    @InjectMocks private LogoutUserCommand command;

    private final User mockedUser = aUser().build();

    @Test
    void execute_whenRefreshTokenIsNull_shouldThrowUserInvalidRefreshTokenException() {
        var params = new LogoutUserCommandParams(mockedUser, null);
        assertThrows(UserInvalidRefreshTokenException.class, () -> command.execute(params));

        verifyNoInteractions(jwtService);
    }

    @Test
    void execute_whenRefreshTokenIsBlank_shouldThrowUserInvalidRefreshTokenException() {
        var params = new LogoutUserCommandParams(mockedUser, "  ");
        assertThrows(UserInvalidRefreshTokenException.class, () -> command.execute(params));

        verifyNoInteractions(jwtService);
    }

    @Test
    void execute_whenRefreshTokenIsNotFound_shouldThrowUserInvalidRefreshTokenException() {
        var params = new LogoutUserCommandParams(mockedUser, "refresh-token");

        when(jwtService.getRefreshTokenEntity(eq(params.refreshToken())))
                .thenReturn(Optional.empty());

        assertThrows(UserInvalidRefreshTokenException.class, () -> command.execute(params));
        verify(jwtService).getRefreshTokenEntity(eq(params.refreshToken()));
        verify(jwtService, never()).revokeRefreshToken(anyString());
    }

    @Test
    void execute_whenTokenBelongsToAnotherUser_shouldThrowForbiddenException() {
        var params = new LogoutUserCommandParams(mockedUser, "refresh-token");

        var otherUser = aUser().withId(UUID.randomUUID()).build();

        var refreshToken = mock(RefreshToken.class);

        when(refreshToken.getUser()).thenReturn(otherUser);
        when(jwtService.getRefreshTokenEntity(eq(params.refreshToken())))
                .thenReturn(Optional.of(refreshToken));

        assertThrows(ForbiddenException.class, () -> command.execute(params));

        verify(jwtService).getRefreshTokenEntity(eq(params.refreshToken()));
        verify(jwtService, never()).revokeRefreshToken(anyString());
    }

    @Test
    void execute_whenValidTokenAndSameUser_shouldRevokeTokenAndReturnNull() {
        var params = new LogoutUserCommandParams(mockedUser, "refresh-token");

        var refreshToken = mock(RefreshToken.class);

        when(refreshToken.getUser()).thenReturn(mockedUser);
        when(jwtService.getRefreshTokenEntity(eq(params.refreshToken())))
                .thenReturn(Optional.of(refreshToken));

        var result = command.execute(params);

        assertNull(result);

        verify(jwtService).getRefreshTokenEntity(eq(params.refreshToken()));
        verify(jwtService).revokeRefreshToken(eq(params.refreshToken()));
    }

    @Test
    void execute_whenUnexpectedExceptionOccurs_shouldRethrowException() {
        var params = new LogoutUserCommandParams(mockedUser, "refresh-token");
        when(jwtService.getRefreshTokenEntity(eq(params.refreshToken())))
                .thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> command.execute(params));
    }
}

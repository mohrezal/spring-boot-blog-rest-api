package com.github.mohrezal.api.domains.users.commands;

import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.users.commands.params.UpdateUserProfileCommandParams;
import com.github.mohrezal.api.domains.users.dtos.UpdateUserProfileRequest;
import com.github.mohrezal.api.domains.users.dtos.UserSummary;
import com.github.mohrezal.api.domains.users.mappers.UserMapper;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.exceptions.types.InvalidRequestException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateUserProfileCommandTest {

    @Mock private UserRepository userRepository;

    @Mock private UserMapper userMapper;

    @InjectMocks private UpdateUserProfileCommand command;

    private final User user = aUser().withId(UUID.randomUUID()).build();

    @Test
    void execute_whenValidRequest_shouldUpdateUserAndReturnSummary() {
        var request = new UpdateUserProfileRequest("John", "Doe", "Bio text");

        var params = new UpdateUserProfileCommandParams(user, request);
        var summary = org.mockito.Mockito.mock(UserSummary.class);

        when(userRepository.save(eq(user))).thenReturn(user);
        when(userMapper.toUserSummary(user)).thenReturn(summary);

        var result = command.execute(params);

        assertNotNull(result);
        assertEquals(summary, result);

        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("Bio text", user.getBio());

        verify(userRepository).save(user);
        verify(userMapper).toUserSummary(user);
    }

    @Test
    void execute_whenFirstNameIsBlank_shouldThrowInvalidRequestException() {
        var request = new UpdateUserProfileRequest(" ", null, null);

        var params = new UpdateUserProfileCommandParams(user, request);

        assertThrows(InvalidRequestException.class, () -> command.execute(params));

        verify(userRepository, never()).save(any());
    }

    @Test
    void execute_whenLastNameIsBlank_shouldThrowInvalidRequestException() {
        var request = new UpdateUserProfileRequest(null, "", null);

        var params = new UpdateUserProfileCommandParams(user, request);

        assertThrows(InvalidRequestException.class, () -> command.execute(params));

        verify(userRepository, never()).save(any());
    }

    @Test
    void execute_whenBioIsBlank_shouldThrowInvalidRequestException() {
        var request = new UpdateUserProfileRequest(null, null, "   ");

        var params = new UpdateUserProfileCommandParams(user, request);

        assertThrows(InvalidRequestException.class, () -> command.execute(params));

        verify(userRepository, never()).save(any());
    }

    @Test
    void execute_whenOnlyOneFieldProvided_shouldUpdateOnlyThatField() {
        var request = new UpdateUserProfileRequest(null, "UpdatedLastName", null);

        var params = new UpdateUserProfileCommandParams(user, request);
        var summary = org.mockito.Mockito.mock(UserSummary.class);

        when(userRepository.save(eq(user))).thenReturn(user);
        when(userMapper.toUserSummary(user)).thenReturn(summary);

        var result = command.execute(params);

        assertNotNull(result);
        assertEquals("UpdatedLastName", user.getLastName());

        verify(userRepository).save(user);
    }
}

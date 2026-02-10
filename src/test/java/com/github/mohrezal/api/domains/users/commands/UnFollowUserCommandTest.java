package com.github.mohrezal.api.domains.users.commands;

import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.users.commands.params.UnFollowUserCommandParams;
import com.github.mohrezal.api.domains.users.exceptions.types.UserCannotFollowOrUnfollowSelfException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFollowingException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.models.UserFollow;
import com.github.mohrezal.api.domains.users.repositories.UserFollowRepository;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnFollowUserCommandTest {

    @Mock private UserFollowRepository userFollowRepository;

    @Mock private UserRepository userRepository;

    @InjectMocks private UnFollowUserCommand command;

    private final User currentUser = aUser().withId(UUID.randomUUID()).build();

    @Test
    void execute_whenUserIsFollowingTarget_shouldUnfollowSuccessfully() {
        var targetUser = aUser().withId(UUID.randomUUID()).build();
        var userFollow = mock(UserFollow.class);

        var params = new UnFollowUserCommandParams(currentUser, targetUser.getHandle());

        when(userRepository.findByHandle(targetUser.getHandle()))
                .thenReturn(Optional.of(targetUser));
        when(userFollowRepository.findByFollowedAndFollower(targetUser, currentUser))
                .thenReturn(Optional.of(userFollow));

        command.execute(params);

        verify(userFollowRepository).delete(userFollow);
    }

    @Test
    void execute_whenTargetUserNotFound_shouldThrowUserNotFoundException() {
        var params = new UnFollowUserCommandParams(currentUser, "unknown-user");

        when(userRepository.findByHandle("unknown-user")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> command.execute(params));

        verifyNoInteractions(userFollowRepository);
    }

    @Test
    void execute_whenUserTriesToUnfollowSelf_shouldThrowException() {
        var params = new UnFollowUserCommandParams(currentUser, currentUser.getHandle());

        when(userRepository.findByHandle(currentUser.getHandle()))
                .thenReturn(Optional.of(currentUser));

        assertThrows(UserCannotFollowOrUnfollowSelfException.class, () -> command.execute(params));

        verifyNoInteractions(userFollowRepository);
    }

    @Test
    void execute_whenUserIsNotFollowingTarget_shouldThrowUserNotFollowingException() {
        var targetUser = aUser().withId(UUID.randomUUID()).build();
        var params = new UnFollowUserCommandParams(currentUser, targetUser.getHandle());

        when(userRepository.findByHandle(targetUser.getHandle()))
                .thenReturn(Optional.of(targetUser));
        when(userFollowRepository.findByFollowedAndFollower(targetUser, currentUser))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFollowingException.class, () -> command.execute(params));

        verify(userFollowRepository, never()).delete(any());
    }
}

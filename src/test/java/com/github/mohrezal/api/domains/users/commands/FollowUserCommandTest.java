package com.github.mohrezal.api.domains.users.commands;

import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.notifications.events.UserFollowedEvent;
import com.github.mohrezal.api.domains.users.commands.params.FollowUserCommandParams;
import com.github.mohrezal.api.domains.users.exceptions.types.UserAlreadyFollowingException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserCannotFollowOrUnfollowSelfException;
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
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class FollowUserCommandTest {

    @Mock private UserFollowRepository userFollowRepository;

    @Mock private UserRepository userRepository;

    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks private FollowUserCommand command;

    private final User follower = aUser().withId(UUID.randomUUID()).withHandle("follower").build();

    @Test
    void execute_whenValidRequest_shouldFollowUserAndPublishEvent() {
        var targetUser = aUser().withId(UUID.randomUUID()).withHandle("target").build();

        var params = new FollowUserCommandParams(follower, targetUser.getHandle());

        when(userRepository.findByHandle(eq(targetUser.getHandle())))
                .thenReturn(Optional.of(targetUser));
        when(userFollowRepository.isAlreadyFollowing(eq(follower.getId()), eq(targetUser.getId())))
                .thenReturn(false);

        command.execute(params);

        verify(userFollowRepository).save(any(UserFollow.class));
        verify(eventPublisher).publishEvent(any(UserFollowedEvent.class));
    }

    @Test
    void execute_whenTargetUserNotFound_shouldThrowUserNotFoundException() {
        var params = new FollowUserCommandParams(follower, "unknown");

        when(userRepository.findByHandle(eq("unknown"))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> command.execute(params));

        verify(userFollowRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void execute_whenFollowingSelf_shouldThrowUserCannotFollowOrUnfollowSelfException() {
        var params = new FollowUserCommandParams(follower, follower.getHandle());

        when(userRepository.findByHandle(eq(follower.getHandle())))
                .thenReturn(Optional.of(follower));

        assertThrows(UserCannotFollowOrUnfollowSelfException.class, () -> command.execute(params));

        verify(userFollowRepository, never()).save(any());
    }

    @Test
    void execute_whenAlreadyFollowing_shouldThrowUserAlreadyFollowingException() {
        var targetUser = aUser().withId(UUID.randomUUID()).withHandle("target").build();

        var params = new FollowUserCommandParams(follower, targetUser.getHandle());

        when(userRepository.findByHandle(eq(targetUser.getHandle())))
                .thenReturn(Optional.of(targetUser));
        when(userFollowRepository.isAlreadyFollowing(eq(follower.getId()), eq(targetUser.getId())))
                .thenReturn(true);

        assertThrows(UserAlreadyFollowingException.class, () -> command.execute(params));

        verify(userFollowRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }
}

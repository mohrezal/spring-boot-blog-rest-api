package com.github.mohrezal.api.domains.posts.commands;

import static com.github.mohrezal.api.support.builders.PostBuilder.aPost;
import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.posts.commands.params.DeletePostCommandParams;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.api.domains.posts.models.Post;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeletePostCommandTest {
    @Mock private PostRepository postRepository;

    @InjectMocks private DeletePostCommand command;

    private final User user = aUser().withId(UUID.randomUUID()).build();

    @Test
    void execute_whenUserIsOwner_shouldDeletePost() {
        var post = aPost().withUser(user).build();
        var params = new DeletePostCommandParams(user, "post-slug");

        when(postRepository.findBySlug("post-slug")).thenReturn(Optional.of(post));

        var result = command.execute(params);

        assertNull(result);

        verify(postRepository, times(1)).findBySlug(anyString());
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    void execute_whenPostDoesNotExist_shouldThrowPostNotFoundException() {
        var params = new DeletePostCommandParams(user, "missing-slug");

        when(postRepository.findBySlug("missing-slug")).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> command.execute(params));

        verify(postRepository, times(1)).findBySlug(anyString());
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    void execute_whenUserIsNotOwner_shouldThrowAccessDeniedException() {
        var anotherUser = aUser().withId(UUID.randomUUID()).build();
        var post = aPost().withUser(user).build();
        var params = new DeletePostCommandParams(anotherUser, "post-slug");

        when(postRepository.findBySlug("post-slug")).thenReturn(Optional.of(post));

        assertThrows(AccessDeniedException.class, () -> command.execute(params));

        verify(postRepository, times(1)).findBySlug(anyString());
        verify(postRepository, never()).delete(any(Post.class));
    }
}

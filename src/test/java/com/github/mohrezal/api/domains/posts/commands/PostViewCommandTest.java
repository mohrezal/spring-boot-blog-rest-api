package com.github.mohrezal.api.domains.posts.commands;

import static com.github.mohrezal.api.support.builders.PostBuilder.aPost;
import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.posts.commands.params.PostViewCommandParams;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.api.domains.posts.models.Post;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.domains.posts.repositories.PostViewRepository;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.shared.services.hash.HashService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostViewCommandTest {

    @Mock private HashService hashService;
    @Mock private PostRepository postRepository;
    @Mock private PostViewRepository postViewRepository;

    @InjectMocks private PostViewCommand command;

    @Test
    void execute_whenPostNotFound_shouldThrowPostNotFoundException() {
        when(postRepository.findBySlug("missing-slug")).thenReturn(Optional.empty());
        var params = new PostViewCommandParams("missing-slug", "vid-1", null);

        assertThrows(PostNotFoundException.class, () -> command.execute(params));
    }

    @Test
    void execute_whenVidIsMissingAndNewView_shouldGenerateVidAndIncrementCount() {
        Post post = aPost().withId(UUID.randomUUID()).withSlug("post-slug").build();

        when(postRepository.findBySlug("post-slug")).thenReturn(Optional.of(post));
        when(hashService.sha256(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn("vid-hash-value");
        when(postViewRepository.insertIgnoreDuplicate(post.getId(), "vid-hash-value", null))
                .thenReturn(1);

        var params = new PostViewCommandParams("post-slug", null, null);
        var result = command.execute(params);

        assertTrue(result.response());
        assertNotNull(result.vid());
        verify(hashService).sha256(result.vid());
        verify(postViewRepository).insertIgnoreDuplicate(post.getId(), "vid-hash-value", null);
        verify(postRepository).incrementViewCount(post.getId());
    }

    @Test
    void execute_whenDuplicateView_shouldReturnFalseAndNotIncrementCount() {
        Post post = aPost().withId(UUID.randomUUID()).withSlug("post-slug").build();

        when(postRepository.findBySlug("post-slug")).thenReturn(Optional.of(post));
        when(hashService.sha256("vid-1")).thenReturn("vid-hash-value");
        when(postViewRepository.insertIgnoreDuplicate(post.getId(), "vid-hash-value", null))
                .thenReturn(0);

        var params = new PostViewCommandParams("post-slug", "vid-1", null);
        var result = command.execute(params);

        assertFalse(result.response());
        assertEquals("vid-1", result.vid());
        verify(postRepository, never()).incrementViewCount(post.getId());
    }

    @Test
    void execute_whenAuthenticatedAndNewView_shouldStoreUserId() {
        User user = aUser().withId(UUID.randomUUID()).build();
        Post post = aPost().withId(UUID.randomUUID()).withSlug("post-slug").build();

        when(postRepository.findBySlug("post-slug")).thenReturn(Optional.of(post));
        when(hashService.sha256("vid-1")).thenReturn("vid-hash-value");
        when(postViewRepository.insertIgnoreDuplicate(post.getId(), "vid-hash-value", user.getId()))
                .thenReturn(1);

        var params = new PostViewCommandParams("post-slug", "vid-1", user);
        var result = command.execute(params);

        assertTrue(result.response());
        verify(postViewRepository)
                .insertIgnoreDuplicate(post.getId(), "vid-hash-value", user.getId());
        verify(postRepository).incrementViewCount(post.getId());
    }
}

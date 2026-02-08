package com.github.mohrezal.api.domains.posts.commands;

import static com.github.mohrezal.api.support.builders.PostBuilder.aPost;
import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.posts.commands.params.UnarchivePostCommandParams;
import com.github.mohrezal.api.domains.posts.enums.PostStatus;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostInvalidStatusTransitionException;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.api.domains.posts.models.Post;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.domains.posts.services.postutils.PostUtilsService;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.services.userutils.UserUtilsService;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnarchivePostCommandTest {
    @Mock private PostRepository postRepository;

    @Mock private PostUtilsService postUtilsService;

    @Mock private UserUtilsService userUtilsService;

    @InjectMocks private UnarchivePostCommand command;

    private final User mockedUser = aUser().build();

    @Test
    void execute_whenUserIsOwnerAndPostIsArchived_shouldUnarchivePost() {
        var post = aPost().withStatus(PostStatus.ARCHIVED).build();
        var params = new UnarchivePostCommandParams(mockedUser, "post-slug");

        when(postRepository.findBySlug("post-slug")).thenReturn(Optional.of(post));
        when(postUtilsService.isOwner(post, mockedUser)).thenReturn(true);

        var result = command.execute(params);

        assertNull(result);
        assertEquals(PostStatus.PUBLISHED, post.getStatus());

        verify(postRepository, times(1)).findBySlug(anyString());
        verify(postRepository, times(1)).save(post);
        verify(postUtilsService, times(1)).isOwner(post, mockedUser);
        verify(userUtilsService, never()).isAdmin(any());
    }

    @Test
    void execute_whenUserIsAdminAndPostIsArchived_shouldUnarchivePost() {
        var post = aPost().withStatus(PostStatus.ARCHIVED).build();
        var params = new UnarchivePostCommandParams(mockedUser, "post-slug");

        when(postRepository.findBySlug("post-slug")).thenReturn(Optional.of(post));
        when(postUtilsService.isOwner(post, mockedUser)).thenReturn(false);
        when(userUtilsService.isAdmin(mockedUser)).thenReturn(true);

        var result = command.execute(params);

        assertNull(result);
        assertEquals(PostStatus.PUBLISHED, post.getStatus());

        verify(postRepository, times(1)).findBySlug(anyString());
        verify(postRepository, times(1)).save(post);
        verify(postUtilsService, times(1)).isOwner(post, mockedUser);
        verify(userUtilsService, times(1)).isAdmin(mockedUser);
    }

    @Test
    void execute_whenUserIsNotOwnerOrAdmin_shouldThrowAccessDeniedException() {
        var post = aPost().withStatus(PostStatus.ARCHIVED).build();
        var params = new UnarchivePostCommandParams(mockedUser, "post-slug");

        when(postRepository.findBySlug("post-slug")).thenReturn(Optional.of(post));
        when(postUtilsService.isOwner(post, mockedUser)).thenReturn(false);
        when(userUtilsService.isAdmin(mockedUser)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> command.execute(params));

        verify(postRepository, times(1)).findBySlug(anyString());
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void execute_whenPostDoesNotExist_shouldThrowPostNotFoundException() {
        var params = new UnarchivePostCommandParams(mockedUser, "missing-slug");

        when(postRepository.findBySlug("missing-slug")).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> command.execute(params));

        verify(postRepository, times(1)).findBySlug(anyString());
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void execute_whenPostIsNotArchived_shouldThrowPostInvalidStatusTransitionException() {
        var post = aPost().withStatus(PostStatus.DRAFT).build();
        var params = new UnarchivePostCommandParams(mockedUser, "post-slug");

        when(postRepository.findBySlug("post-slug")).thenReturn(Optional.of(post));
        when(postUtilsService.isOwner(post, mockedUser)).thenReturn(true);

        assertThrows(PostInvalidStatusTransitionException.class, () -> command.execute(params));

        verify(postRepository, times(1)).findBySlug(anyString());
        verify(postRepository, never()).save(any(Post.class));
    }
}

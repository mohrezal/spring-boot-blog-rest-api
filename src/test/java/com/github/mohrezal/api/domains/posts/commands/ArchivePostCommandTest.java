package com.github.mohrezal.api.domains.posts.commands;

import static com.github.mohrezal.api.support.builders.PostBuilder.aPost;
import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.posts.commands.params.ArchivePostCommandParams;
import com.github.mohrezal.api.domains.posts.enums.PostStatus;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostInvalidStatusTransitionException;
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
class ArchivePostCommandTest {

    @Mock private PostRepository postRepository;

    @Mock private PostUtilsService postUtilsService;

    @Mock private UserUtilsService userUtilsService;

    @InjectMocks private ArchivePostCommand command;

    private final User mockedUser = aUser().build();

    @Test
    void execute_whenPublishedPostAndUserIsOwner_shouldArchivePost() {
        var params = new ArchivePostCommandParams(mockedUser, "post-slug");

        var post = aPost().withStatus(PostStatus.PUBLISHED).build();
        var archivedPost = aPost().withStatus(PostStatus.PUBLISHED).build();

        when(postRepository.findBySlug("post-slug")).thenReturn(Optional.ofNullable(post));
        when(postUtilsService.isOwner(post, mockedUser)).thenReturn(true);
        when(postRepository.save(any(Post.class))).thenReturn(archivedPost);

        var result = command.execute(params);

        assertNull(result);

        verify(postRepository, times(1)).findBySlug(anyString());
        verify(postRepository, times(1)).save(any(Post.class));
        verify(postUtilsService, times(1)).isOwner(any(Post.class), any(User.class));
        verify(userUtilsService, times(0)).isAdmin(any(User.class));
    }

    @Test
    void execute_whenPublishedPostAndUserIsAdmin_shouldArchivePost() {
        var params = new ArchivePostCommandParams(mockedUser, "post-slug");

        var post = aPost().withStatus(PostStatus.PUBLISHED).build();
        var archivedPost = aPost().withStatus(PostStatus.PUBLISHED).build();

        when(postRepository.findBySlug("post-slug")).thenReturn(Optional.ofNullable(post));
        when(postUtilsService.isOwner(post, mockedUser)).thenReturn(false);
        when(userUtilsService.isAdmin(mockedUser)).thenReturn(true);
        when(postRepository.save(any(Post.class))).thenReturn(archivedPost);

        var result = command.execute(params);

        assertNull(result);

        verify(postRepository, times(1)).findBySlug(anyString());
        verify(postRepository, times(1)).save(any(Post.class));
        verify(userUtilsService, times(1)).isAdmin(any(User.class));
        verify(postUtilsService, times(1)).isOwner(any(Post.class), any(User.class));
    }

    @Test
    void execute_whenUserIsNotOwnerOrAdmin_shouldThrowAccessDeniedException() {
        var params = new ArchivePostCommandParams(mockedUser, "post-slug");

        var post = aPost().withStatus(PostStatus.PUBLISHED).build();
        var archivedPost = aPost().withStatus(PostStatus.PUBLISHED).build();

        when(postRepository.findBySlug("post-slug")).thenReturn(Optional.ofNullable(post));
        when(postUtilsService.isOwner(post, mockedUser)).thenReturn(false);
        when(userUtilsService.isAdmin(mockedUser)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> command.execute(params));

        verify(postRepository, times(1)).findBySlug(anyString());
        verify(postRepository, times(0)).save(any(Post.class));
        verify(userUtilsService, times(1)).isAdmin(any(User.class));
        verify(postUtilsService, times(1)).isOwner(any(Post.class), any(User.class));
    }

    @Test
    void execute_whenPostIsNotPublished_shouldThrowInvalidStatusTransitionException() {
        var params = new ArchivePostCommandParams(mockedUser, "post-slug");

        var post = aPost().withStatus(PostStatus.DRAFT).build();

        when(postRepository.findBySlug("post-slug")).thenReturn(Optional.ofNullable(post));
        when(postUtilsService.isOwner(post, mockedUser)).thenReturn(true);

        assertThrows(PostInvalidStatusTransitionException.class, () -> command.execute(params));

        verify(postRepository, times(1)).findBySlug(anyString());
        verify(postRepository, times(0)).save(any(Post.class));
        verify(postUtilsService, times(1)).isOwner(any(Post.class), any(User.class));
    }
}

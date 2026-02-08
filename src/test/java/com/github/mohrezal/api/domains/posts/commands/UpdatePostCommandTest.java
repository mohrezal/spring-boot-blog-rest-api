package com.github.mohrezal.api.domains.posts.commands;

import static com.github.mohrezal.api.support.builders.PostBuilder.aPost;
import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.categories.exceptions.types.CategoryNotFoundException;
import com.github.mohrezal.api.domains.categories.models.Category;
import com.github.mohrezal.api.domains.categories.repositories.CategoryRepository;
import com.github.mohrezal.api.domains.posts.commands.params.UpdatePostCommandParams;
import com.github.mohrezal.api.domains.posts.dtos.PostDetail;
import com.github.mohrezal.api.domains.posts.dtos.UpdatePostRequest;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostSlugAlreadyExistsException;
import com.github.mohrezal.api.domains.posts.mappers.PostMapper;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.domains.posts.services.postutils.PostUtilsService;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import com.github.mohrezal.api.shared.exceptions.types.ResourceConflictException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class UpdatePostCommandTest {
    @Mock private PostRepository postRepository;

    @Mock private PostMapper postMapper;

    @Mock private CategoryRepository categoryRepository;

    @Mock private PostUtilsService postUtilsService;

    @InjectMocks private UpdatePostCommand command;

    private final User mockedUser = aUser().build();

    @Test
    void execute_whenPostDoesNotExist_shouldThrowPostNotFoundException() {
        var request =
                new UpdatePostRequest(
                        "Test title",
                        "Test content",
                        "MOCKED_AVATAR",
                        Set.of(UUID.randomUUID()),
                        "Test description",
                        "post-slug");

        var params = new UpdatePostCommandParams(mockedUser, request, "post-slug");

        when(postRepository.findBySlug("post-slug")).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> command.execute(params));

        verify(postRepository).findBySlug("post-slug");
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    void execute_whenCurrentUserIsNotOwner_shouldThrowAccessDeniedException() {
        var post = aPost().build();

        var request =
                new UpdatePostRequest(
                        "Test title",
                        "Test content",
                        "MOCKED_AVATAR",
                        Set.of(UUID.randomUUID()),
                        "Test description",
                        "post-slug");

        var params = new UpdatePostCommandParams(mockedUser, request, "post-slug");

        when(postRepository.findBySlug("post-slug")).thenReturn(Optional.of(post));

        when(postUtilsService.isOwner(post, mockedUser)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> command.execute(params));

        verify(postUtilsService).isOwner(post, mockedUser);
        verify(postRepository, never()).save(any());
    }

    @Test
    void execute_whenSomeCategoriesNotFound_shouldThrowCategoryNotFoundException() {
        var post = aPost().build();

        var categoryId1 = UUID.randomUUID();
        var categoryId2 = UUID.randomUUID();

        var request =
                new UpdatePostRequest(
                        "Test title",
                        "Test content",
                        "MOCKED_AVATAR",
                        Set.of(categoryId1, categoryId2),
                        "Test description",
                        "post-slug");

        var params = new UpdatePostCommandParams(mockedUser, request, "post-slug");

        when(postRepository.findBySlug("post-slug")).thenReturn(Optional.of(post));

        when(postUtilsService.isOwner(post, mockedUser)).thenReturn(true);
        when(categoryRepository.findAllByIdIn(Set.of(categoryId1, categoryId2)))
                .thenReturn(Set.of(mock(Category.class)));

        assertThrows(CategoryNotFoundException.class, () -> command.execute(params));

        verify(postUtilsService).isOwner(post, mockedUser);
        verify(categoryRepository).findAllByIdIn(Set.of(categoryId1, categoryId2));
        verify(postRepository, never()).save(any());
    }

    @Test
    void
            execute_whenSlugIsChangedAndNewSlugAlreadyExists_shouldThrowPostSlugAlreadyExistsException() {
        var post = aPost().withSlug("old-slug").build();

        var categoryId = UUID.randomUUID();

        var request =
                new UpdatePostRequest(
                        "Updated title",
                        "Updated content",
                        "UPDATED_AVATAR",
                        Set.of(categoryId),
                        "Updated description",
                        "new-slug");

        var params = new UpdatePostCommandParams(mockedUser, request, "old-slug");

        when(postRepository.findBySlug("old-slug")).thenReturn(Optional.of(post));

        when(postUtilsService.isOwner(post, mockedUser)).thenReturn(true);

        when(categoryRepository.findAllByIdIn(Set.of(categoryId)))
                .thenReturn(Set.of(mock(Category.class)));

        when(postRepository.existsBySlug("new-slug")).thenReturn(true);

        assertThrows(PostSlugAlreadyExistsException.class, () -> command.execute(params));

        verify(postRepository, never()).save(any());
    }

    @Test
    void execute_whenValidRequest_shouldUpdatePostAndReturnPostDetail() {
        var post = aPost().withSlug("old-slug").build();
        var savedPost = aPost().build();

        var categoryId = UUID.randomUUID();
        var category = mock(Category.class);

        var request =
                new UpdatePostRequest(
                        "Updated title",
                        "Updated content",
                        "UPDATED_AVATAR",
                        Set.of(categoryId),
                        "Updated description",
                        "old-slug");

        var params = new UpdatePostCommandParams(mockedUser, request, "old-slug");

        var postDetail = mock(PostDetail.class);

        when(postRepository.findBySlug("old-slug")).thenReturn(Optional.of(post));

        when(postUtilsService.isOwner(post, mockedUser)).thenReturn(true);

        when(categoryRepository.findAllByIdIn(Set.of(categoryId))).thenReturn(Set.of(category));

        when(postRepository.save(post)).thenReturn(savedPost);

        when(postMapper.toPostDetail(savedPost)).thenReturn(postDetail);

        var result = command.execute(params);

        assertNotNull(result);
        assertEquals(postDetail, result);

        verify(postMapper).toTargetPost(request, post);
        verify(postRepository).save(post);
    }

    @Test
    void execute_whenDataIntegrityViolationOccurs_shouldThrowResourceConflictException() {
        var post = aPost().build();

        var categoryId = UUID.randomUUID();

        var request =
                new UpdatePostRequest(
                        "Updated title",
                        "Updated content",
                        "UPDATED_AVATAR",
                        Set.of(categoryId),
                        "Updated description",
                        "post-slug");

        var params = new UpdatePostCommandParams(mockedUser, request, "post-slug");

        when(postRepository.findBySlug("post-slug")).thenReturn(Optional.of(post));

        when(postUtilsService.isOwner(post, mockedUser)).thenReturn(true);

        when(categoryRepository.findAllByIdIn(Set.of(categoryId)))
                .thenReturn(Set.of(mock(Category.class)));

        when(postRepository.save(post)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(ResourceConflictException.class, () -> command.execute(params));
    }
}

package com.github.mohrezal.api.domains.posts.commands;

import static com.github.mohrezal.api.support.builders.CategoryBuilder.aCategory;
import static com.github.mohrezal.api.support.builders.PostBuilder.aPost;
import static com.github.mohrezal.api.support.builders.PostDetailBuilder.aPostDetail;
import static com.github.mohrezal.api.support.builders.RedirectBuilder.aRedirect;
import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.categories.repositories.CategoryRepository;
import com.github.mohrezal.api.domains.posts.commands.params.CreatePostCommandParams;
import com.github.mohrezal.api.domains.posts.dtos.CreatePostRequest;
import com.github.mohrezal.api.domains.posts.enums.PostLanguage;
import com.github.mohrezal.api.domains.posts.enums.PostStatus;
import com.github.mohrezal.api.domains.posts.mappers.PostMapper;
import com.github.mohrezal.api.domains.posts.models.Post;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.domains.redirects.models.Redirect;
import com.github.mohrezal.api.domains.redirects.repositories.RedirectRepository;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.shared.exceptions.types.ResourceConflictException;
import com.github.mohrezal.api.shared.services.sluggenerator.SlugGeneratorService;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class CreatePostCommandTest {

    @Mock private PostRepository postRepository;

    @Mock private CategoryRepository categoryRepository;

    @Mock private SlugGeneratorService slugGeneratorService;

    @Mock private RedirectRepository redirectRepository;

    @Mock private PostMapper postMapper;

    @InjectMocks private CreatePostCommand command;

    private final User mockedUser = aUser().build();

    @Test
    void execute_whenRequestIsValid_shouldReturnPostDetail() {
        var categoryId = UUID.randomUUID();
        var category = aCategory().withId(categoryId).build();

        var request =
                new CreatePostRequest(
                        "Test post title",
                        "This is post content",
                        "TEST_AVATAR_URL",
                        Set.of(categoryId),
                        "This is post description",
                        PostLanguage.ENGLISH);

        var params = new CreatePostCommandParams(mockedUser, request);

        var post = aPost().withTitle(request.title()).build();

        var postDetails = aPostDetail().build();
        var savedPostId = UUID.randomUUID();
        var savedPost = aPost().withId(savedPostId).build();

        when(categoryRepository.findAllByIdIn(Set.of(categoryId))).thenReturn(Set.of(category));

        when(postMapper.toPost(request)).thenReturn(post);

        when(slugGeneratorService.getSlug(any(), any())).thenReturn("new-post-slug");
        when(slugGeneratorService.getRandomSlug(anyInt(), any())).thenReturn("abcd");

        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        ArgumentCaptor<Redirect> redirectCaptor = ArgumentCaptor.forClass(Redirect.class);
        when(redirectRepository.save(redirectCaptor.capture()))
                .thenReturn(aRedirect().withCode("abcd").withTargetId(savedPostId).build());

        when(postMapper.toPostDetail(savedPost, "abcd")).thenReturn(postDetails);

        var result = command.execute(params);

        assertEquals(postDetails, result);
        assertEquals(PostStatus.DRAFT, post.getStatus());
        assertEquals("new-post-slug", post.getSlug());
        assertEquals("abcd", redirectCaptor.getValue().getCode());
        assertEquals(savedPostId, redirectCaptor.getValue().getTargetId());

        verify(slugGeneratorService).getRandomSlug(eq(12), any());
    }

    @Test
    void
            execute_whenRepositoryDataIntegrityViolationException_showThrowResourceConflictException() {
        var categoryId = UUID.randomUUID();
        var category = aCategory().withId(categoryId).build();

        var request =
                new CreatePostRequest(
                        "Test post title",
                        "This is post content",
                        "TEST_AVATAR_URL",
                        Set.of(categoryId),
                        "This is post description",
                        PostLanguage.ENGLISH);

        var params = new CreatePostCommandParams(mockedUser, request);

        var post = aPost().withTitle(request.title()).build();

        when(categoryRepository.findAllByIdIn(Set.of(categoryId))).thenReturn(Set.of(category));

        when(postMapper.toPost(request)).thenReturn(post);

        when(slugGeneratorService.getSlug(any(), any())).thenReturn("new-post-slug");

        when(postRepository.save(any(Post.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(ResourceConflictException.class, () -> command.execute(params));

        verify(redirectRepository, never()).save(any(Redirect.class));
        verify(postMapper, never()).toPostDetail(any(Post.class), anyString());
    }
}

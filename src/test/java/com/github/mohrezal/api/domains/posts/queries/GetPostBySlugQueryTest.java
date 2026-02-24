package com.github.mohrezal.api.domains.posts.queries;

import static com.github.mohrezal.api.support.builders.PostBuilder.aPost;
import static com.github.mohrezal.api.support.builders.PostDetailBuilder.aPostDetail;
import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.posts.enums.PostStatus;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.api.domains.posts.mappers.PostMapper;
import com.github.mohrezal.api.domains.posts.queries.params.GetPostBySlugQueryParams;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.domains.posts.services.postutils.PostUtilsService;
import com.github.mohrezal.api.domains.redirects.enums.RedirectTargetType;
import com.github.mohrezal.api.domains.redirects.services.store.RedirectStoreService;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.services.userutils.UserUtilsService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetPostBySlugQueryTest {

    @Mock private PostRepository postRepository;

    @Mock private PostMapper postMapper;

    @Mock private PostUtilsService postUtilsService;

    @Mock private UserUtilsService userUtilsService;

    @Mock private RedirectStoreService redirectStoreService;

    @InjectMocks private GetPostBySlugQuery query;

    private final User mockedUser = aUser().withEmail("test@gmail.com").build();

    @Test
    void execute_whenPostDoesNotExist_shouldThrowPostNotFoundException() {
        var params = new GetPostBySlugQueryParams(mockedUser, "missing-post");

        when(postRepository.findBySlug(params.slug())).thenReturn(Optional.empty());
        assertThrows(PostNotFoundException.class, () -> query.execute(params));
    }

    @Test
    void execute_whenPostIsPublished_shouldReturnPostDetail() {
        var params = new GetPostBySlugQueryParams(mockedUser, "published-post");

        var postId = java.util.UUID.randomUUID();
        var post = aPost().withId(postId).withStatus(PostStatus.PUBLISHED).build();
        var postDetail = aPostDetail().build();
        when(postRepository.findBySlug(params.slug())).thenReturn(Optional.of(post));
        when(redirectStoreService.findCodeByTargetTypeAndTargetId(RedirectTargetType.POST, postId))
                .thenReturn(Optional.of("abcd"));
        when(postMapper.toPostDetail(post, "abcd")).thenReturn(postDetail);
        var result = query.execute(params);

        assertEquals(postDetail, result);
    }

    @Test
    void execute_whenPostIsDraftAndUserIsAdmin_shouldReturnPostDetail() {
        var params = new GetPostBySlugQueryParams(mockedUser, "draft-post");

        var postId = java.util.UUID.randomUUID();
        var post = aPost().withId(postId).withStatus(PostStatus.DRAFT).build();

        var postDetail = aPostDetail().build();

        when(postRepository.findBySlug(params.slug())).thenReturn(Optional.of(post));
        when(redirectStoreService.findCodeByTargetTypeAndTargetId(RedirectTargetType.POST, postId))
                .thenReturn(Optional.of("abcd"));
        when(userUtilsService.isAdmin(mockedUser)).thenReturn(true);
        when(postMapper.toPostDetail(post, "abcd")).thenReturn(postDetail);

        var result = query.execute(params);

        assertEquals(postDetail, result);
    }

    @Test
    void execute_whenPostIsDraftAndUserIsOwner_shouldReturnPostDetail() {
        var params = new GetPostBySlugQueryParams(mockedUser, "draft-post");

        var postId = java.util.UUID.randomUUID();
        var post = aPost().withId(postId).withStatus(PostStatus.DRAFT).build();

        var postDetail = aPostDetail().build();

        when(postRepository.findBySlug(params.slug())).thenReturn(Optional.of(post));
        when(redirectStoreService.findCodeByTargetTypeAndTargetId(RedirectTargetType.POST, postId))
                .thenReturn(Optional.of("abcd"));
        when(userUtilsService.isAdmin(mockedUser)).thenReturn(false);
        when(postUtilsService.isOwner(post, mockedUser)).thenReturn(true);
        when(postMapper.toPostDetail(post, "abcd")).thenReturn(postDetail);

        var result = query.execute(params);

        assertEquals(postDetail, result);
    }
}

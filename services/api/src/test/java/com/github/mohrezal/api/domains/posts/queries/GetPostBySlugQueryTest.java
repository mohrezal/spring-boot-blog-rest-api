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
import com.github.mohrezal.api.domains.privilege.constant.Permissions;
import com.github.mohrezal.api.domains.privilege.service.SecurityPermissionChecker;
import com.github.mohrezal.api.domains.users.models.User;
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

    @Mock private SecurityPermissionChecker securityPermissionChecker;

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

        var post = aPost().withStatus(PostStatus.PUBLISHED).build();
        var postDetail = aPostDetail().build();
        when(postRepository.findBySlug(params.slug())).thenReturn(Optional.of(post));
        when(postMapper.toPostDetail(post)).thenReturn(postDetail);
        var result = query.execute(params);

        assertEquals(postDetail, result);
    }

    @Test
    void execute_whenPostIsDraftAndUserIsNotOwner_shouldThrowPostNotFoundException() {
        var params = new GetPostBySlugQueryParams(mockedUser, "draft-post");

        var post = aPost().withStatus(PostStatus.DRAFT).build();

        when(postRepository.findBySlug(params.slug())).thenReturn(Optional.of(post));
        when(postUtilsService.isOwner(post, mockedUser)).thenReturn(false);
        when(securityPermissionChecker.hasPermission(Permissions.BLOG_POSTS_MODERATE))
                .thenReturn(false);

        assertThrows(PostNotFoundException.class, () -> query.execute(params));
    }

    @Test
    void execute_whenPostIsDraftAndUserHasModeratePermission_shouldReturnPostDetail() {
        var params = new GetPostBySlugQueryParams(mockedUser, "draft-post");

        var post = aPost().withStatus(PostStatus.DRAFT).build();
        var postDetail = aPostDetail().build();

        when(postRepository.findBySlug(params.slug())).thenReturn(Optional.of(post));
        when(postUtilsService.isOwner(post, mockedUser)).thenReturn(false);
        when(securityPermissionChecker.hasPermission(Permissions.BLOG_POSTS_MODERATE))
                .thenReturn(true);
        when(postMapper.toPostDetail(post)).thenReturn(postDetail);

        var result = query.execute(params);

        assertEquals(postDetail, result);
    }

    @Test
    void execute_whenPostIsDraftAndUserIsOwner_shouldReturnPostDetail() {
        var params = new GetPostBySlugQueryParams(mockedUser, "draft-post");

        var post = aPost().withStatus(PostStatus.DRAFT).build();

        var postDetail = aPostDetail().build();

        when(postRepository.findBySlug(params.slug())).thenReturn(Optional.of(post));
        when(postUtilsService.isOwner(post, mockedUser)).thenReturn(true);
        when(postMapper.toPostDetail(post)).thenReturn(postDetail);

        var result = query.execute(params);

        assertEquals(postDetail, result);
    }
}

package com.github.mohrezal.api.domains.posts.queries;

import com.github.mohrezal.api.domains.posts.dtos.PostDetail;
import com.github.mohrezal.api.domains.posts.enums.PostStatus;
import com.github.mohrezal.api.domains.posts.exceptions.context.PostGetBySlugExceptionContext;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.api.domains.posts.mappers.PostMapper;
import com.github.mohrezal.api.domains.posts.queries.params.GetPostBySlugQueryParams;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.domains.posts.services.postutils.PostUtilsService;
import com.github.mohrezal.api.domains.redirects.enums.RedirectTargetType;
import com.github.mohrezal.api.domains.redirects.models.Redirect;
import com.github.mohrezal.api.domains.redirects.repositories.RedirectRepository;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.services.userutils.UserUtilsService;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedQuery;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetPostBySlugQuery extends AuthenticatedQuery<GetPostBySlugQueryParams, PostDetail> {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostUtilsService postUtilsService;
    private final UserUtilsService userUtilsService;
    private final RedirectRepository redirectRepository;

    @Transactional(readOnly = true)
    @Override
    public PostDetail execute(GetPostBySlugQueryParams params) {
        UUID currentUserId = null;
        User currentUser = null;
        if (params.getUserDetails() != null) {
            currentUser = getCurrentUser(params);
            currentUserId = currentUser.getId();
        }
        var context = new PostGetBySlugExceptionContext(currentUserId, params.slug());
        var post =
                this.postRepository
                        .findBySlug(params.slug())
                        .orElseThrow(() -> new PostNotFoundException(context));
        var redirectCode =
                this.redirectRepository
                        .findByTargetTypeAndTargetId(RedirectTargetType.POST, post.getId())
                        .map(Redirect::getCode)
                        .orElse(null);
        log.info("redirectCode is : {}", redirectCode);
        if (post.getStatus().equals(PostStatus.PUBLISHED)) {
            log.info("Get post by slug query successful.");
            return this.postMapper.toPostDetail(post, redirectCode);
        }

        var isAdmin = currentUser != null && userUtilsService.isAdmin(currentUser);
        var isOwner = currentUser != null && postUtilsService.isOwner(post, currentUser);

        if (isAdmin || isOwner) {
            log.info("Get post by slug query successful.");
            return this.postMapper.toPostDetail(post, redirectCode);
        }

        throw new PostNotFoundException(context);
    }
}

package com.github.mohrezal.api.domains.posts.queries;

import com.github.mohrezal.api.domains.posts.dtos.PostDetail;
import com.github.mohrezal.api.domains.posts.enums.PostStatus;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.api.domains.posts.mappers.PostMapper;
import com.github.mohrezal.api.domains.posts.queries.params.GetPostBySlugQueryParams;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.domains.posts.services.postutils.PostUtilsService;
import com.github.mohrezal.api.domains.users.services.userutils.UserUtilsService;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Slf4j
public class GetPostBySlugQuery extends AuthenticatedQuery<GetPostBySlugQueryParams, PostDetail> {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostUtilsService postUtilsService;
    private final UserUtilsService userUtilsService;

    @Transactional(readOnly = true)
    @Override
    public PostDetail execute(GetPostBySlugQueryParams params) {
        validate(params);
        var post =
                this.postRepository
                        .findBySlug(params.slug())
                        .orElseThrow(PostNotFoundException::new);

        if (post.getStatus().equals(PostStatus.PUBLISHED)) {
            return this.postMapper.toPostDetail(post);
        }

        var isAdmin = userUtilsService.isAdmin(user);
        var isOwner = postUtilsService.isOwner(post, user);

        if (isAdmin || isOwner) {
            return this.postMapper.toPostDetail(post);
        }

        throw new PostNotFoundException();
    }
}

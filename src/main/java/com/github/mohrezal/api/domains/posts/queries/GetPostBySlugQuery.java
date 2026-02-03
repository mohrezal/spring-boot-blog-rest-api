package com.github.mohrezal.api.domains.posts.queries;

import com.github.mohrezal.api.domains.posts.dtos.PostDetail;
import com.github.mohrezal.api.domains.posts.enums.PostStatus;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.api.domains.posts.mappers.PostMapper;
import com.github.mohrezal.api.domains.posts.models.Post;
import com.github.mohrezal.api.domains.posts.queries.params.GetPostBySlugQueryParams;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.domains.posts.services.postutils.PostUtilsService;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.services.userutils.UserUtilsService;
import com.github.mohrezal.api.shared.interfaces.Query;
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
public class GetPostBySlugQuery implements Query<GetPostBySlugQueryParams, PostDetail> {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostUtilsService postUtilsService;
    private final UserUtilsService userUtilsService;

    @Transactional(readOnly = true)
    @Override
    public PostDetail execute(GetPostBySlugQueryParams params) {
        Post post =
                this.postRepository
                        .findBySlug(params.getSlug())
                        .orElseThrow(PostNotFoundException::new);

        if (post.getStatus().equals(PostStatus.PUBLISHED)) {
            return this.postMapper.toPostDetail(post);
        }

        if (params.getUserDetails() == null) {
            throw new PostNotFoundException();
        }

        User user = (User) params.getUserDetails();

        boolean isAdmin = userUtilsService.isAdmin(user);
        boolean isOwner = postUtilsService.isOwner(post, user);

        if (isAdmin || isOwner) {
            return this.postMapper.toPostDetail(post);
        }

        throw new PostNotFoundException();
    }
}

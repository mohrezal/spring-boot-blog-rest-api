package com.github.mohrezal.springbootblogrestapi.domains.posts.queries;

import com.github.mohrezal.springbootblogrestapi.domains.posts.dtos.PostDetail;
import com.github.mohrezal.springbootblogrestapi.domains.posts.enums.PostStatus;
import com.github.mohrezal.springbootblogrestapi.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.springbootblogrestapi.domains.posts.mappers.PostMapper;
import com.github.mohrezal.springbootblogrestapi.domains.posts.models.Post;
import com.github.mohrezal.springbootblogrestapi.domains.posts.queries.params.GetPostBySlugQueryParams;
import com.github.mohrezal.springbootblogrestapi.domains.posts.repositories.PostRepository;
import com.github.mohrezal.springbootblogrestapi.domains.users.enums.UserRole;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Query;
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

        boolean isAdmin = user.getRole().equals(UserRole.ADMIN);
        boolean isOwner = user.getId().equals(post.getUser().getId());

        if (isAdmin || isOwner) {
            return this.postMapper.toPostDetail(post);
        }

        throw new PostNotFoundException();
    }
}

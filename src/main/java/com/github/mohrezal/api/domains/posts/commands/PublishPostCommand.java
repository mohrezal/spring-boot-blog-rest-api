package com.github.mohrezal.api.domains.posts.commands;

import com.github.mohrezal.api.domains.posts.commands.params.PublishPostCommandParams;
import com.github.mohrezal.api.domains.posts.enums.PostStatus;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostInvalidStatusTransitionException;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.api.domains.posts.models.Post;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.domains.posts.services.postutils.PostUtilsService;
import com.github.mohrezal.api.domains.users.services.userutils.UserUtilsService;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedCommand;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import java.time.OffsetDateTime;
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
public class PublishPostCommand extends AuthenticatedCommand<PublishPostCommandParams, Void> {

    private final PostRepository postRepository;
    private final PostUtilsService postUtilsService;
    private final UserUtilsService userUtilsService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void execute(PublishPostCommandParams params) {
        validate(params);

        Post post =
                postRepository.findBySlug(params.slug()).orElseThrow(PostNotFoundException::new);

        if (!postUtilsService.isOwner(post, user) && !userUtilsService.isAdmin(user)) {
            throw new AccessDeniedException();
        }
        if (!post.getStatus().equals(PostStatus.DRAFT)) {
            throw new PostInvalidStatusTransitionException();
        }
        post.setStatus(PostStatus.PUBLISHED);
        post.setPublishedAt(OffsetDateTime.now());
        postRepository.save(post);
        return null;
    }
}

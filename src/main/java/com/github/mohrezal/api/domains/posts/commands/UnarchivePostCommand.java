package com.github.mohrezal.api.domains.posts.commands;

import com.github.mohrezal.api.domains.posts.commands.params.UnarchivePostCommandParams;
import com.github.mohrezal.api.domains.posts.enums.PostStatus;
import com.github.mohrezal.api.domains.posts.exceptions.context.PostUnarchiveExceptionContext;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostInvalidStatusTransitionException;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.domains.posts.services.postutils.PostUtilsService;
import com.github.mohrezal.api.domains.users.services.userutils.UserUtilsService;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedCommand;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
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
public class UnarchivePostCommand extends AuthenticatedCommand<UnarchivePostCommandParams, Void> {

    private final PostRepository postRepository;
    private final PostUtilsService postUtilsService;
    private final UserUtilsService userUtilsService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void execute(UnarchivePostCommandParams params) {
        validate(params);
        var context = new PostUnarchiveExceptionContext(getUserId(), params.slug());

        var post =
                postRepository
                        .findBySlug(params.slug())
                        .orElseThrow(() -> new PostNotFoundException(context));

        if (!postUtilsService.isOwner(post, user) && !userUtilsService.isAdmin(user)) {
            throw new AccessDeniedException(context);
        }
        if (!post.getStatus().equals(PostStatus.ARCHIVED)) {
            throw new PostInvalidStatusTransitionException(context);
        }
        post.setStatus(PostStatus.PUBLISHED);
        postRepository.save(post);
        log.info("Unarchive post successful.");
        return null;
    }
}

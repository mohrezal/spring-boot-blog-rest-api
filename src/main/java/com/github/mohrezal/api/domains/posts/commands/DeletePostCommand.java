package com.github.mohrezal.api.domains.posts.commands;

import com.github.mohrezal.api.domains.posts.commands.params.DeletePostCommandParams;
import com.github.mohrezal.api.domains.posts.exceptions.context.PostDeleteExceptionContext;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedCommand;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Service
@Slf4j
@RequiredArgsConstructor
public class DeletePostCommand extends AuthenticatedCommand<DeletePostCommandParams, Void> {

    private final PostRepository postRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void execute(DeletePostCommandParams params) {
        validate(params);

        var context = new PostDeleteExceptionContext(getUserId(), params.slug());
        var post =
                postRepository
                        .findBySlug(params.slug())
                        .orElseThrow(() -> new PostNotFoundException(context));

        if (!user.getId().equals(post.getUser().getId())) {
            throw new AccessDeniedException(context);
        }

        postRepository.delete(post);
        log.info("Delete post successful.");
        return null;
    }
}

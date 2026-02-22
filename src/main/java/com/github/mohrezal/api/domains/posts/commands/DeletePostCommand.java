package com.github.mohrezal.api.domains.posts.commands;

import com.github.mohrezal.api.domains.posts.commands.params.DeletePostCommandParams;
import com.github.mohrezal.api.domains.posts.exceptions.context.PostDeleteExceptionContext;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.domains.redirects.enums.RedirectTargetType;
import com.github.mohrezal.api.domains.redirects.repositories.RedirectRepository;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedCommand;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeletePostCommand extends AuthenticatedCommand<DeletePostCommandParams, Void> {

    private final PostRepository postRepository;
    private final RedirectRepository redirectRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void execute(DeletePostCommandParams params) {
        var currentUser = getCurrentUser(params);

        var context = new PostDeleteExceptionContext(currentUser.getId(), params.slug());
        var post =
                postRepository
                        .findBySlug(params.slug())
                        .orElseThrow(() -> new PostNotFoundException(context));

        if (!currentUser.getId().equals(post.getUser().getId())) {
            throw new AccessDeniedException(context);
        }

        redirectRepository.deleteByTargetTypeAndTargetId(RedirectTargetType.POST, post.getId());
        postRepository.delete(post);

        log.info("Delete post successful.");
        return null;
    }
}

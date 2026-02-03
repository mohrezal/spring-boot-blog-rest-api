package com.github.mohrezal.springbootblogrestapi.domains.posts.commands;

import com.github.mohrezal.springbootblogrestapi.domains.posts.commands.params.DeletePostCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.springbootblogrestapi.domains.posts.models.Post;
import com.github.mohrezal.springbootblogrestapi.domains.posts.repositories.PostRepository;
import com.github.mohrezal.springbootblogrestapi.shared.abstracts.AuthenticatedCommand;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.AccessDeniedException;
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

        Post post =
                postRepository.findBySlug(params.slug()).orElseThrow(PostNotFoundException::new);

        if (!user.getId().equals(post.getUser().getId())) {
            throw new AccessDeniedException();
        }

        postRepository.delete(post);

        return null;
    }
}

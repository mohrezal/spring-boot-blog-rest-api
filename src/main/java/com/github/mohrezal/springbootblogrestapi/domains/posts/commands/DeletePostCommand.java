package com.github.mohrezal.springbootblogrestapi.domains.posts.commands;

import com.github.mohrezal.springbootblogrestapi.domains.posts.commands.params.DeletePostCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.springbootblogrestapi.domains.posts.models.Post;
import com.github.mohrezal.springbootblogrestapi.domains.posts.repositories.PostRepository;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.AccessDeniedException;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Command;
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
public class DeletePostCommand implements Command<DeletePostCommandParams, Void> {
    private final PostRepository postRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void execute(DeletePostCommandParams params) {
        Post post =
                postRepository.findBySlug(params.getSlug()).orElseThrow(PostNotFoundException::new);
        User user = (User) params.getUserDetails();

        if (!user.getId().equals(post.getUser().getId())) {
            throw new AccessDeniedException();
        }

        postRepository.delete(post);

        return null;
    }
}

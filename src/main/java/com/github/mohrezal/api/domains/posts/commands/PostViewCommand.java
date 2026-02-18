package com.github.mohrezal.api.domains.posts.commands;

import com.github.mohrezal.api.domains.posts.commands.params.PostViewCommandParams;
import com.github.mohrezal.api.domains.posts.dtos.PostViewCommandResponse;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.domains.posts.repositories.PostViewRepository;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.shared.interfaces.Command;
import com.github.mohrezal.api.shared.services.hash.HashService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostViewCommand implements Command<PostViewCommandParams, PostViewCommandResponse> {

    private final HashService hashService;
    private final PostRepository postRepository;
    private final PostViewRepository postViewRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PostViewCommandResponse execute(PostViewCommandParams params) {
        var post = postRepository.findBySlug(params.slug()).orElseThrow(PostNotFoundException::new);

        var vid = params.vid();

        if (vid == null || vid.isBlank()) {
            vid = UUID.randomUUID().toString();
        }

        var vidHash = hashService.sha256(vid);
        UUID currentUserId = params.getUserDetails() instanceof User user ? user.getId() : null;

        int inserted =
                postViewRepository.insertIgnoreDuplicate(post.getId(), vidHash, currentUserId);

        if (inserted > 0) {
            postRepository.incrementViewCount(post.getId());
            return new PostViewCommandResponse(true, vid);
        }

        return new PostViewCommandResponse(false, vid);
    }
}

package com.github.mohrezal.api.domains.posts.commands;

import com.github.mohrezal.api.domains.categories.exceptions.types.CategoryNotFoundException;
import com.github.mohrezal.api.domains.categories.repositories.CategoryRepository;
import com.github.mohrezal.api.domains.posts.commands.params.UpdatePostCommandParams;
import com.github.mohrezal.api.domains.posts.dtos.PostDetail;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostSlugAlreadyExistsException;
import com.github.mohrezal.api.domains.posts.mappers.PostMapper;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.domains.posts.services.postutils.PostUtilsService;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedCommand;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import com.github.mohrezal.api.shared.exceptions.types.ResourceConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Slf4j
public class UpdatePostCommand extends AuthenticatedCommand<UpdatePostCommandParams, PostDetail> {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final CategoryRepository categoryRepository;
    private final PostUtilsService postUtilsService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PostDetail execute(UpdatePostCommandParams params) {
        validate(params);

        var post =
                this.postRepository
                        .findBySlug(params.slug())
                        .orElseThrow(PostNotFoundException::new);

        if (!postUtilsService.isOwner(post, user)) {
            throw new AccessDeniedException();
        }

        var categories = categoryRepository.findAllByIdIn(params.updatePostRequest().categoryIds());

        if (categories.size() != params.updatePostRequest().categoryIds().size()) {
            throw new CategoryNotFoundException();
        }

        if (!post.getSlug().equals(params.updatePostRequest().slug())) {
            if (postRepository.existsBySlug(params.updatePostRequest().slug())) {
                throw new PostSlugAlreadyExistsException();
            }
        }

        postMapper.toTargetPost(params.updatePostRequest(), post);
        post.setCategories(categories);

        try {
            var savedPost = postRepository.save(post);
            return this.postMapper.toPostDetail(savedPost);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceConflictException();
        }
    }
}

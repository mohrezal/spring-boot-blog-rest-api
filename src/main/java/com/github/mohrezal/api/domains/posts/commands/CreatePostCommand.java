package com.github.mohrezal.api.domains.posts.commands;

import com.github.mohrezal.api.domains.categories.exceptions.types.CategoryNotFoundException;
import com.github.mohrezal.api.domains.categories.repositories.CategoryRepository;
import com.github.mohrezal.api.domains.posts.commands.params.CreatePostCommandParams;
import com.github.mohrezal.api.domains.posts.dtos.PostDetail;
import com.github.mohrezal.api.domains.posts.enums.PostStatus;
import com.github.mohrezal.api.domains.posts.mappers.PostMapper;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedCommand;
import com.github.mohrezal.api.shared.exceptions.types.ResourceConflictException;
import com.github.mohrezal.api.shared.services.sluggenerator.SlugGeneratorService;
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
public class CreatePostCommand extends AuthenticatedCommand<CreatePostCommandParams, PostDetail> {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final SlugGeneratorService slugGeneratorService;

    private final PostMapper postMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PostDetail execute(CreatePostCommandParams params) {
        validate(params);

        try {
            var categoryIds = params.createPostRequest().categoryIds();
            var categories = this.categoryRepository.findAllByIdIn(categoryIds);

            if (categories.size() != categoryIds.size()) {
                throw new CategoryNotFoundException();
            }

            var newPost = this.postMapper.toPost(params.createPostRequest());
            newPost.setCategories(categories);
            newPost.setUser(user);
            newPost.setStatus(PostStatus.DRAFT);
            var slug =
                    slugGeneratorService.getSlug(newPost.getTitle(), postRepository::existsBySlug);
            newPost.setSlug(slug);
            var savedPost = postRepository.save(newPost);

            log.info("Create post successful.");
            return this.postMapper.toPostDetail(savedPost);
        } catch (CategoryNotFoundException | ResourceConflictException ex) {
            log.warn("Create post failed - message: {}", ex.getMessage());
            throw ex;
        } catch (DataIntegrityViolationException ex) {
            log.warn("Create post failed - data integrity violation");
            throw new ResourceConflictException();
        } catch (Exception ex) {
            log.error("Unexpected error during create post operation", ex);
            throw ex;
        }
    }
}

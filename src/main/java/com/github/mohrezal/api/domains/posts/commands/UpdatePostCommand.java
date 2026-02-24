package com.github.mohrezal.api.domains.posts.commands;

import com.github.mohrezal.api.domains.categories.exceptions.types.CategoryNotFoundException;
import com.github.mohrezal.api.domains.categories.repositories.CategoryRepository;
import com.github.mohrezal.api.domains.posts.commands.params.UpdatePostCommandParams;
import com.github.mohrezal.api.domains.posts.dtos.PostDetail;
import com.github.mohrezal.api.domains.posts.exceptions.context.PostUpdateExceptionContext;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostSlugAlreadyExistsException;
import com.github.mohrezal.api.domains.posts.mappers.PostMapper;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.domains.posts.services.postutils.PostUtilsService;
import com.github.mohrezal.api.domains.redirects.enums.RedirectTargetType;
import com.github.mohrezal.api.domains.redirects.services.store.RedirectStoreService;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedCommand;
import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import com.github.mohrezal.api.shared.exceptions.types.ResourceConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdatePostCommand extends AuthenticatedCommand<UpdatePostCommandParams, PostDetail> {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final CategoryRepository categoryRepository;
    private final PostUtilsService postUtilsService;
    private final RedirectStoreService redirectStoreService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PostDetail execute(UpdatePostCommandParams params) {
        var currentUser = getCurrentUser(params);
        var request = params.updatePostRequest();
        var context = new PostUpdateExceptionContext(currentUser.getId(), params.slug());

        try {
            var post =
                    this.postRepository
                            .findBySlug(params.slug())
                            .orElseThrow(() -> new PostNotFoundException(context));

            if (!postUtilsService.isOwner(post, currentUser)) {
                throw new AccessDeniedException(context);
            }

            var categories = categoryRepository.findAllByIdIn(request.categoryIds());

            if (categories.size() != request.categoryIds().size()) {
                throw new CategoryNotFoundException(context);
            }

            if (!post.getSlug().equals(request.slug())) {
                if (postRepository.existsBySlug(request.slug())) {
                    throw new PostSlugAlreadyExistsException(context);
                }
            }

            postMapper.toTargetPost(request, post);
            post.setCategories(categories);

            var savedPost = postRepository.save(post);

            log.info("Update post successful.");

            var redirectCode =
                    this.redirectStoreService
                            .findCodeByTargetTypeAndTargetId(RedirectTargetType.POST, post.getId())
                            .orElse(null);
            return this.postMapper.toPostDetail(savedPost, redirectCode);
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceConflictException(
                    MessageKey.SHARED_ERROR_RESOURCE_CONFLICT, context, ex);
        }
    }
}

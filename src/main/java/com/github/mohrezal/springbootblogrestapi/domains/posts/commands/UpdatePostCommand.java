package com.github.mohrezal.springbootblogrestapi.domains.posts.commands;

import com.github.mohrezal.springbootblogrestapi.domains.categories.exceptions.types.CategoryNotFoundException;
import com.github.mohrezal.springbootblogrestapi.domains.categories.models.Category;
import com.github.mohrezal.springbootblogrestapi.domains.categories.repositories.CategoryRepository;
import com.github.mohrezal.springbootblogrestapi.domains.posts.commands.params.UpdatePostCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.posts.dtos.PostDetail;
import com.github.mohrezal.springbootblogrestapi.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.springbootblogrestapi.domains.posts.exceptions.types.PostSlugAlreadyExistsException;
import com.github.mohrezal.springbootblogrestapi.domains.posts.mappers.PostMapper;
import com.github.mohrezal.springbootblogrestapi.domains.posts.models.Post;
import com.github.mohrezal.springbootblogrestapi.domains.posts.repositories.PostRepository;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.AccessDeniedException;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.ResourceConflictException;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Command;
import java.util.Set;
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
public class UpdatePostCommand implements Command<UpdatePostCommandParams, PostDetail> {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final CategoryRepository categoryRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PostDetail execute(UpdatePostCommandParams params) {
        Post post =
                this.postRepository
                        .findBySlug(params.getSlug())
                        .orElseThrow(PostNotFoundException::new);

        User user = (User) params.getUserDetails();

        if (!post.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException();
        }

        Set<Category> categories =
                categoryRepository.findAllByIdIn(params.getUpdatePostRequest().getCategoryIds());

        if (categories.size() != params.getUpdatePostRequest().getCategoryIds().size()) {
            throw new CategoryNotFoundException();
        }

        if (!post.getSlug().equals(params.getUpdatePostRequest().getSlug())) {
            if (postRepository.existsBySlug(params.getUpdatePostRequest().getSlug())) {
                throw new PostSlugAlreadyExistsException();
            }
        }

        postMapper.toTargetPost(params.getUpdatePostRequest(), post);
        post.setCategories(categories);

        try {
            Post savedPost = postRepository.save(post);
            return this.postMapper.toPostDetail(savedPost);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceConflictException();
        }
    }
}

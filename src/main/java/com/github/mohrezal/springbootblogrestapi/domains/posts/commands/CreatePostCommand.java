package com.github.mohrezal.springbootblogrestapi.domains.posts.commands;

import com.github.mohrezal.springbootblogrestapi.domains.categories.exceptions.types.CategoryNotFoundException;
import com.github.mohrezal.springbootblogrestapi.domains.categories.models.Category;
import com.github.mohrezal.springbootblogrestapi.domains.categories.repositories.CategoryRepository;
import com.github.mohrezal.springbootblogrestapi.domains.posts.commands.params.CreatePostCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.posts.dtos.PostDetail;
import com.github.mohrezal.springbootblogrestapi.domains.posts.enums.PostStatus;
import com.github.mohrezal.springbootblogrestapi.domains.posts.mappers.PostMapper;
import com.github.mohrezal.springbootblogrestapi.domains.posts.models.Post;
import com.github.mohrezal.springbootblogrestapi.domains.posts.repositories.PostRepository;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Command;
import com.github.mohrezal.springbootblogrestapi.shared.services.sluggenerator.SlugGeneratorService;
import java.util.Set;
import java.util.UUID;
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
public class CreatePostCommand implements Command<CreatePostCommandParams, PostDetail> {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final SlugGeneratorService slugGeneratorService;

    private final PostMapper postMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PostDetail execute(CreatePostCommandParams params) {

        User currentUser = (User) params.getUserDetails();

        Set<UUID> categoryIds = params.getCreatePostRequest().getCategoryIds();
        Set<Category> categories = this.categoryRepository.findAllByIdIn(categoryIds);

        if (categories.size() != categoryIds.size()) {
            throw new CategoryNotFoundException();
        }

        Post newPost = this.postMapper.toPost(params.getCreatePostRequest());
        newPost.setCategories(categories);
        newPost.setUser(currentUser);
        newPost.setStatus(PostStatus.DRAFT);
        String slug =
                slugGeneratorService.getSlug(newPost.getTitle(), postRepository::existsBySlug);
        newPost.setSlug(slug);
        Post savedPost = this.postRepository.save(newPost);
        return this.postMapper.toPostDetail(savedPost);
    }
}

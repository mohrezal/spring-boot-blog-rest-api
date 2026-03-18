package com.github.mohrezal.api.domains.categories.commands;

import com.github.mohrezal.api.domains.categories.commands.params.CreateCategoryCommandParams;
import com.github.mohrezal.api.domains.categories.dtos.CategorySummary;
import com.github.mohrezal.api.domains.categories.exceptions.types.CategoryParentNotfoundException;
import com.github.mohrezal.api.domains.categories.mappers.CategoryMapper;
import com.github.mohrezal.api.domains.categories.repositories.CategoryRepository;
import com.github.mohrezal.api.domains.users.enums.UserRole;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedCommand;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import com.github.mohrezal.api.shared.services.sluggenerator.SlugGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateCategoryCommand
        extends AuthenticatedCommand<CreateCategoryCommandParams, CategorySummary> {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final SlugGeneratorService slugGeneratorService;

    @Override
    public CategorySummary execute(CreateCategoryCommandParams params) {
        var user = getCurrentUser(params);
        if (!user.getRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException();
        }
        var slug =
                slugGeneratorService.getSlug(
                        params.createCategoryRequest().slug(), categoryRepository::existsBySlug);
        var parentId = params.createCategoryRequest().parentId();
        var mappedRequest = categoryMapper.toCategory(params.createCategoryRequest(), slug);
        var parent =
                parentId != null
                        ? categoryRepository
                                .findById(parentId)
                                .orElseThrow(CategoryParentNotfoundException::new)
                        : null;
        mappedRequest.setParent(parent);
        var savedCategory = categoryRepository.save(mappedRequest);

        return categoryMapper.toCategorySummary(savedCategory);
    }
}

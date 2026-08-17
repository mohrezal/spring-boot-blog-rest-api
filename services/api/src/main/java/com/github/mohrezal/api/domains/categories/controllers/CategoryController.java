package com.github.mohrezal.api.domains.categories.controllers;

import com.github.mohrezal.api.config.Routes;
import com.github.mohrezal.api.domains.categories.commands.CreateCategoryCommand;
import com.github.mohrezal.api.domains.categories.commands.params.CreateCategoryCommandParams;
import com.github.mohrezal.api.domains.categories.dtos.CategoryDetail;
import com.github.mohrezal.api.domains.categories.dtos.CategorySummary;
import com.github.mohrezal.api.domains.categories.dtos.CreateCategoryRequest;
import com.github.mohrezal.api.domains.categories.queries.GetCategoriesQuery;
import com.github.mohrezal.api.domains.categories.queries.params.GetCategoriesQueryParams;
import com.github.mohrezal.api.domains.privilege.constant.Permissions;
import com.github.mohrezal.api.shared.annotations.RequiresPermission;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.Category.BASE)
@RequiredArgsConstructor
@Tag(name = "Category")
public class CategoryController {
    private final GetCategoriesQuery categoriesQueries;
    private final CreateCategoryCommand createCategoryCommand;

    @GetMapping
    public ResponseEntity<Set<CategoryDetail>> getCategories(
            @RequestParam(required = false) UUID parentId) {
        var params = new GetCategoriesQueryParams(parentId);
        return ResponseEntity.ok().body(categoriesQueries.execute(params));
    }

    @PostMapping
    @RequiresPermission(Permissions.BLOG_CATEGORIES_CREATE)
    public ResponseEntity<CategorySummary> createCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CreateCategoryRequest createCategoryRequest) {
        var params = new CreateCategoryCommandParams(userDetails, createCategoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createCategoryCommand.execute(params));
    }
}

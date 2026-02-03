package com.github.mohrezal.api.domains.categories.controllers;

import com.github.mohrezal.api.config.Routes;
import com.github.mohrezal.api.domains.categories.dtos.CategorySummary;
import com.github.mohrezal.api.domains.categories.queries.GetCategoriesQuery;
import com.github.mohrezal.api.domains.categories.queries.params.GetCategoriesQueryParams;
import com.github.mohrezal.api.shared.dtos.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.Category.BASE)
@RequiredArgsConstructor
@Tag(name = "Category")
public class CategoryController {
    private final ObjectProvider<@NonNull GetCategoriesQuery> categoriesQueries;

    @GetMapping
    public ResponseEntity<@NonNull PageResponse<CategorySummary>> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        GetCategoriesQueryParams params =
                GetCategoriesQueryParams.builder().page(page).size(size).build();
        var query = this.categoriesQueries.getObject();
        return ResponseEntity.ok().body(query.execute(params));
    }
}

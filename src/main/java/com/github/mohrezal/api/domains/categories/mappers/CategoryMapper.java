package com.github.mohrezal.api.domains.categories.mappers;

import com.github.mohrezal.api.domains.categories.dtos.CategorySummary;
import com.github.mohrezal.api.domains.categories.dtos.CreateCategoryRequest;
import com.github.mohrezal.api.domains.categories.models.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategorySummary toCategorySummary(Category category);

    @Mapping(target = "parent", ignore = true)
    @Mapping(source = "slug", target = "slug")
    @Mapping(source = "request.name", target = "name")
    @Mapping(source = "request.description", target = "description")
    Category toCategory(CreateCategoryRequest request, String slug);
}

package com.github.mohrezal.api.domains.categories.mappers;

import com.github.mohrezal.api.domains.categories.dtos.CategorySummary;
import com.github.mohrezal.api.domains.categories.models.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategorySummary toCategorySummary(Category category);
}

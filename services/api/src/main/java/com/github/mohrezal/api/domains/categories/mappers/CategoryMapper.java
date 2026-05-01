package com.github.mohrezal.api.domains.categories.mappers;

import com.github.mohrezal.api.domains.categories.dtos.CategoryChildrenInfo;
import com.github.mohrezal.api.domains.categories.dtos.CategoryDetail;
import com.github.mohrezal.api.domains.categories.dtos.CategorySummary;
import com.github.mohrezal.api.domains.categories.dtos.CreateCategoryRequest;
import com.github.mohrezal.api.domains.categories.models.Category;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategorySummary toCategorySummary(Category category);

    Set<CategorySummary> toCategorySummaries(Set<Category> categories);

    @Mapping(target = "parent", ignore = true)
    @Mapping(source = "slug", target = "slug")
    @Mapping(source = "request.name", target = "name")
    @Mapping(source = "request.description", target = "description")
    Category toCategory(CreateCategoryRequest request, String slug);

    @Mapping(target = "id", source = "category.id")
    @Mapping(target = "name", source = "category.name")
    @Mapping(target = "slug", source = "category.slug")
    @Mapping(target = "description", source = "category.description")
    @Mapping(target = "hasChildren", source = "hasChildren")
    @Mapping(target = "createdAt", source = "category.createdAt")
    @Mapping(target = "updatedAt", source = "category.updatedAt")
    CategoryDetail toCategoryDetail(CategoryChildrenInfo info);

    Set<CategoryDetail> toCategoryDetails(List<CategoryChildrenInfo> childrenInfos);
}

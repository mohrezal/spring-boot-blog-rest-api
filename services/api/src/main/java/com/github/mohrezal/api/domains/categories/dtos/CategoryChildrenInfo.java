package com.github.mohrezal.api.domains.categories.dtos;

import com.github.mohrezal.api.domains.categories.models.Category;

public interface CategoryChildrenInfo {
    Category getCategory();

    Boolean getHasChildren();
}

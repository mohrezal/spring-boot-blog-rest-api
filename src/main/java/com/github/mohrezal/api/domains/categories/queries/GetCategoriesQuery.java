package com.github.mohrezal.api.domains.categories.queries;

import com.github.mohrezal.api.domains.categories.dtos.CategoryDetail;
import com.github.mohrezal.api.domains.categories.mappers.CategoryMapper;
import com.github.mohrezal.api.domains.categories.queries.params.GetCategoriesQueryParams;
import com.github.mohrezal.api.domains.categories.repositories.CategoryRepository;
import com.github.mohrezal.api.shared.interfaces.Query;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetCategoriesQuery implements Query<GetCategoriesQueryParams, Set<CategoryDetail>> {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    @Override
    public Set<CategoryDetail> execute(GetCategoriesQueryParams params) {
        var categories = categoryRepository.findCategoriesWithHasChildren(params.parentId());

        return this.categoryMapper.toCategoryDetails(categories);
    }
}

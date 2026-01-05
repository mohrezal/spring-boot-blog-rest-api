package com.github.mohrezal.springbootblogrestapi.domains.categories.queries;

import com.github.mohrezal.springbootblogrestapi.domains.categories.dtos.CategorySummary;
import com.github.mohrezal.springbootblogrestapi.domains.categories.mappers.CategoryMapper;
import com.github.mohrezal.springbootblogrestapi.domains.categories.queries.params.GetCategoriesQueryParams;
import com.github.mohrezal.springbootblogrestapi.domains.categories.repositories.CategoryRepository;
import com.github.mohrezal.springbootblogrestapi.shared.dtos.PageResponse;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Slf4j
public class GetCategoriesQuery
        implements Query<GetCategoriesQueryParams, PageResponse<CategorySummary>> {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    @Override
    public PageResponse<CategorySummary> execute(GetCategoriesQueryParams params) {
        Pageable pageable = PageRequest.of(params.getPage(), params.getSize());

        return PageResponse.from(
                categoryRepository.findAll(pageable), categoryMapper::toCategorySummary);
    }
}

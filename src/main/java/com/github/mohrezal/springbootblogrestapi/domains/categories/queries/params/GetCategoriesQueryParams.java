package com.github.mohrezal.springbootblogrestapi.domains.categories.queries.params;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetCategoriesQueryParams {
    private int page;
    private int size;
}

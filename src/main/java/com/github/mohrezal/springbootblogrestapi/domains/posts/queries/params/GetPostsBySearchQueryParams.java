package com.github.mohrezal.springbootblogrestapi.domains.posts.queries.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class GetPostsBySearchQueryParams {
    private String query;
    private int size;
    private int page;
}

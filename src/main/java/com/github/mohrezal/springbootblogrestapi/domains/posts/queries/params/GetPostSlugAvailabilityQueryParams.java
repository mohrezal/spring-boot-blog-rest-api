package com.github.mohrezal.springbootblogrestapi.domains.posts.queries.params;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetPostSlugAvailabilityQueryParams {
    private String slug;
}

package com.github.mohrezal.api.domains.posts.queries.params;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetPostSlugAvailabilityQueryParams {
    private String slug;
}

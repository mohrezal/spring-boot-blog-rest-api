package com.github.mohrezal.api.domains.posts.queries.params;

import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetPostsQueryParams {
    private int page;
    private int size;
    private Set<String> categorySlugs;
    private Set<UUID> authorIds;
}

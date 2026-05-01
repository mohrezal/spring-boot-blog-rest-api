package com.github.mohrezal.api.domains.posts.queries.params;

import java.util.Set;
import java.util.UUID;

public record GetPostsQueryParams(
        int page, int size, Set<String> categorySlugs, Set<UUID> authorIds) {}

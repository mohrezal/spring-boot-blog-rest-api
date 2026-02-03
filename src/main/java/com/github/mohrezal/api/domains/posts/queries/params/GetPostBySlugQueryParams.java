package com.github.mohrezal.api.domains.posts.queries.params;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
public class GetPostBySlugQueryParams {
    private String slug;
    private UserDetails userDetails;
}

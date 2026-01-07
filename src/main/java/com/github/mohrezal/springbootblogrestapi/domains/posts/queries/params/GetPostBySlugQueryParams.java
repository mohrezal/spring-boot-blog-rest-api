package com.github.mohrezal.springbootblogrestapi.domains.posts.queries.params;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
public class GetPostBySlugQueryParams {
    private String slug;
    private UserDetails userDetails;
}

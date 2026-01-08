package com.github.mohrezal.springbootblogrestapi.domains.posts.commands.params;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
public class ArchivePostCommandParams {
    private String slug;
    private UserDetails userDetails;
}

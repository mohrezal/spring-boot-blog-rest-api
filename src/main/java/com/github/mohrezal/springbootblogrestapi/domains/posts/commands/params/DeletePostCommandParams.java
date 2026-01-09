package com.github.mohrezal.springbootblogrestapi.domains.posts.commands.params;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
public class DeletePostCommandParams {
    private final String slug;
    private final UserDetails userDetails;
}

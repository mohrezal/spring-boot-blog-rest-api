package com.github.mohrezal.springbootblogrestapi.domains.posts.commands.params;

import com.github.mohrezal.springbootblogrestapi.domains.posts.dtos.UpdatePostRequest;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
public class UpdatePostCommandParams {
    private UserDetails userDetails;
    private UpdatePostRequest updatePostRequest;
    private String slug;
}

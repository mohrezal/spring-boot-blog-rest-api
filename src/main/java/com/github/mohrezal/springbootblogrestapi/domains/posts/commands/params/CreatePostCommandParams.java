package com.github.mohrezal.springbootblogrestapi.domains.posts.commands.params;

import com.github.mohrezal.springbootblogrestapi.domains.posts.dtos.CreatePostRequest;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
public class CreatePostCommandParams {
    private UserDetails userDetails;
    private CreatePostRequest createPostRequest;
}

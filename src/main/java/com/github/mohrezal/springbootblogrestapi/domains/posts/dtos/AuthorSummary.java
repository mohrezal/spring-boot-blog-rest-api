package com.github.mohrezal.springbootblogrestapi.domains.posts.dtos;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorSummary {
    private UUID id;
    private String firstName;
    private String lastName;
    private String avatarUrl;
}

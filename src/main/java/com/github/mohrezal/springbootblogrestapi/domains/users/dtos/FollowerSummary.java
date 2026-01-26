package com.github.mohrezal.springbootblogrestapi.domains.users.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowerSummary {
    private UUID id;
    private String handle;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private boolean isFollowing;
    private OffsetDateTime followedAt;
}

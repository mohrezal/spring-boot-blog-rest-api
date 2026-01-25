package com.github.mohrezal.springbootblogrestapi.domains.users.dtos;

import com.github.mohrezal.springbootblogrestapi.domains.users.enums.UserRole;
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
public class UserSummary {

    private UUID id;
    private String email;
    private String handle;
    private String firstName;
    private String lastName;
    private String bio;
    private String avatarUrl;
    private UserRole role;
    private Boolean isVerified;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

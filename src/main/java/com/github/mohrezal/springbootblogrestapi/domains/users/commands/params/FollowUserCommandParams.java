package com.github.mohrezal.springbootblogrestapi.domains.users.commands.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FollowUserCommandParams {
    private UserDetails userDetails;
    private String handle;
}

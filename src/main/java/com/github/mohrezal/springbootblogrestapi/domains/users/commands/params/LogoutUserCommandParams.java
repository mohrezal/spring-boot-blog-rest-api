package com.github.mohrezal.springbootblogrestapi.domains.users.commands.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogoutUserCommandParams {
    private UserDetails userDetails;
    private String refreshToken;
}

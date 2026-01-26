package com.github.mohrezal.springbootblogrestapi.domains.users.commands.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UploadProfileCommandParams {
    private UserDetails userDetails;
    private UploadProfileRequest uploadProfileRequest;
}

package com.github.mohrezal.springbootblogrestapi.domains.users.commands.params;

import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.LoginRequest;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginUserCommandParams {
    private LoginRequest loginRequest;
    private String ipAddress;
    private String userAgent;
}

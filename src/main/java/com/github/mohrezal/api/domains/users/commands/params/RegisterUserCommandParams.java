package com.github.mohrezal.api.domains.users.commands.params;

import com.github.mohrezal.api.domains.users.dtos.RegisterUserRequest;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterUserCommandParams {
    private RegisterUserRequest registerUserRequest;
    private String ipAddress;
    private String userAgent;
}

package com.github.mohrezal.api.domains.users.commands.params;

import com.github.mohrezal.api.domains.users.dtos.RegisterUserRequest;

public record RegisterUserCommandParams(
        RegisterUserRequest registerUserRequest, String ipAddress, String userAgent) {}

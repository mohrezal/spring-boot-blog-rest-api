package com.github.mohrezal.api.domains.users.commands.params;

import com.github.mohrezal.api.domains.users.dtos.LoginRequest;

public record LoginUserCommandParams(
        LoginRequest loginRequest, String ipAddress, String userAgent, String vid) {}

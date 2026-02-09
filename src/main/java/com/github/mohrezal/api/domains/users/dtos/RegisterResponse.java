package com.github.mohrezal.api.domains.users.dtos;

public record RegisterResponse(UserSummary user, AuthResponse authResponse) {}

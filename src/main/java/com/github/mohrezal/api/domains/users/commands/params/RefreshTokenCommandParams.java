package com.github.mohrezal.api.domains.users.commands.params;

public record RefreshTokenCommandParams(String refreshToken, String ipAddress, String userAgent) {}

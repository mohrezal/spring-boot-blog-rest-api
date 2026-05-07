package com.github.mohrezal.api.domains.users.commands.params;

public record VerifyEmailCommandParams(String token, String redirectUrl) {}

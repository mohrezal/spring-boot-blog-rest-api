package com.github.mohrezal.api.domains.users.exceptions.context;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import java.util.UUID;

public record UserAlreadyVerifiedExceptionContext(UUID userId) implements ExceptionContext {}

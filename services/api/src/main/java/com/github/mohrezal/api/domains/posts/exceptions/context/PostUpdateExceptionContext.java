package com.github.mohrezal.api.domains.posts.exceptions.context;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import java.util.UUID;

public record PostUpdateExceptionContext(UUID userId, String slug) implements ExceptionContext {}

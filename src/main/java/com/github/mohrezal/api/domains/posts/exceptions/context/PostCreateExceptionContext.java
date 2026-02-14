package com.github.mohrezal.api.domains.posts.exceptions.context;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;

public record PostCreateExceptionContext(String userId) implements ExceptionContext {}

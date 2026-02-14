package com.github.mohrezal.api.domains.posts.exceptions.context;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;

public record PostGetBySlugExceptionContext(String userId, String slug)
        implements ExceptionContext {}

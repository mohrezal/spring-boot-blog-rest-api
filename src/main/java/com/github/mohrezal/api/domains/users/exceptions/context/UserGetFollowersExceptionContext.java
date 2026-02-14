package com.github.mohrezal.api.domains.users.exceptions.context;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import java.util.UUID;

public record UserGetFollowersExceptionContext(UUID userId, String targetHandle, int page, int size)
        implements ExceptionContext {}

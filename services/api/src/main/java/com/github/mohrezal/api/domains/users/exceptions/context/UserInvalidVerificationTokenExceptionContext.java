package com.github.mohrezal.api.domains.users.exceptions.context;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;

public record UserInvalidVerificationTokenExceptionContext(String token)
        implements ExceptionContext {}

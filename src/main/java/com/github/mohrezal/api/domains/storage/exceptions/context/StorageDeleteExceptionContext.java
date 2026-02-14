package com.github.mohrezal.api.domains.storage.exceptions.context;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import java.util.UUID;

public record StorageDeleteExceptionContext(UUID userId, String filename)
        implements ExceptionContext {}

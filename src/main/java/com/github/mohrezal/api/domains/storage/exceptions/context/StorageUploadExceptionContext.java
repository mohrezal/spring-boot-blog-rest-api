package com.github.mohrezal.api.domains.storage.exceptions.context;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import java.util.UUID;

public record StorageUploadExceptionContext(
        UUID userId, String filename, long fileSize, String type) implements ExceptionContext {}

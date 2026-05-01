package com.github.mohrezal.api.domains.storage.exceptions.context;

import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;

public record StorageGetByFilenameExceptionContext(String filename) implements ExceptionContext {}

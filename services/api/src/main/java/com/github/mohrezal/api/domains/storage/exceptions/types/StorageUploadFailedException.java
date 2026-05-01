package com.github.mohrezal.api.domains.storage.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.context.ExceptionContext;
import com.github.mohrezal.api.shared.exceptions.types.InternalException;

public class StorageUploadFailedException extends InternalException {
    public StorageUploadFailedException() {
        super(MessageKey.STORAGE_UPLOAD_FAILED);
    }

    public StorageUploadFailedException(ExceptionContext context) {
        super(MessageKey.STORAGE_UPLOAD_FAILED, context);
    }

    public StorageUploadFailedException(ExceptionContext context, Throwable cause) {
        super(MessageKey.STORAGE_UPLOAD_FAILED, context, cause);
    }
}

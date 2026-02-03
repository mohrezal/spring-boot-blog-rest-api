package com.github.mohrezal.api.domains.storage.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.types.InvalidRequestException;

public class StorageInvalidMimeTypeException extends InvalidRequestException {
    public StorageInvalidMimeTypeException() {
        super(MessageKey.STORAGE_FILE_MIME_TYPE_NOT_ALLOWED);
    }
}

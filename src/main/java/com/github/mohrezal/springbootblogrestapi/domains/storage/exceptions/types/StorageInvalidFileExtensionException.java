package com.github.mohrezal.springbootblogrestapi.domains.storage.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.InvalidRequestException;

public class StorageInvalidFileExtensionException extends InvalidRequestException {
    public StorageInvalidFileExtensionException() {
        super(MessageKey.STORAGE_FILE_EXTENSION_NOT_ALLOWED);
    }
}

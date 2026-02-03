package com.github.mohrezal.api.domains.storage.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.types.InvalidRequestException;

public class StorageFileSizeExceededException extends InvalidRequestException {
    public StorageFileSizeExceededException() {
        super(MessageKey.STORAGE_FILE_SIZE_EXCEEDED);
    }
}

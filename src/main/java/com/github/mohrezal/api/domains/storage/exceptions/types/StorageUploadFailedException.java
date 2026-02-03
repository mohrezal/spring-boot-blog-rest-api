package com.github.mohrezal.api.domains.storage.exceptions.types;

import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.types.InternalException;

public class StorageUploadFailedException extends InternalException {
    public StorageUploadFailedException() {
        super(MessageKey.STORAGE_UPLOAD_FAILED);
    }
}

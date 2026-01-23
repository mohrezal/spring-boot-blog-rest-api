package com.github.mohrezal.springbootblogrestapi.domains.storage.exceptions.types;

import com.github.mohrezal.springbootblogrestapi.shared.enums.MessageKey;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.InternalException;

public class StorageUploadFailedException extends InternalException {
    public StorageUploadFailedException() {
        super(MessageKey.STORAGE_UPLOAD_FAILED);
    }
}

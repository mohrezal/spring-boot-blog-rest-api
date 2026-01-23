package com.github.mohrezal.springbootblogrestapi.domains.storage.services.storageutils;

import org.springframework.web.multipart.MultipartFile;

public interface StorageUtils {
    boolean isValidFileExtension(MultipartFile file);

    boolean isMaxFileSizeExceeded(long size);
}

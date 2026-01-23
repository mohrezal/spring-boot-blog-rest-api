package com.github.mohrezal.springbootblogrestapi.domains.storage.services.storageutils;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface StorageUtils {
    boolean isValidMimeType(MultipartFile file) throws IOException;

    boolean isMaxFileSizeExceeded(long size);

    String getMimeType(MultipartFile file) throws IOException;

    String generateFilename(String originalFilename);

    String getExtension(String filename);
}

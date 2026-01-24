package com.github.mohrezal.springbootblogrestapi.domains.storage.services.storageutils;

import com.github.mohrezal.springbootblogrestapi.domains.storage.models.Storage;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface StorageUtilsService {
    boolean isValidMimeType(MultipartFile file) throws IOException;

    boolean isMaxFileSizeExceeded(long size);

    String getMimeType(MultipartFile file) throws IOException;

    String generateFilename(String originalFilename);

    String getExtension(String filename);

    boolean isOwner(User user, Storage storage);
}

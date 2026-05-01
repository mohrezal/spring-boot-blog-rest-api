package com.github.mohrezal.api.domains.storage.services.storageutils;

import com.github.mohrezal.api.domains.storage.models.Storage;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.shared.config.ApplicationProperties;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageUtilsServiceImpl implements StorageUtilsService {

    private static final Tika TIKA = new Tika();
    private final ApplicationProperties applicationProperties;

    @Override
    public boolean isValidMimeType(MultipartFile file) throws IOException {
        String detectedMimeType = getMimeType(file);
        return applicationProperties.storage().allowedMimeTypes().contains(detectedMimeType);
    }

    @Override
    public boolean isMaxFileSizeExceeded(long size) {
        return size > applicationProperties.storage().maxFileSize().toBytes();
    }

    @Override
    public String getMimeType(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            return TIKA.detect(inputStream, file.getOriginalFilename());
        }
    }

    @Override
    public String generateFilename(String originalFilename) {
        String extension = getExtension(originalFilename);
        if (extension.isEmpty()) {
            return UUID.randomUUID().toString();
        }
        return UUID.randomUUID() + "." + extension;
    }

    @Override
    public String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    @Override
    public boolean isOwner(User user, Storage storage) {
        return storage.getUser().getId().equals(user.getId());
    }
}

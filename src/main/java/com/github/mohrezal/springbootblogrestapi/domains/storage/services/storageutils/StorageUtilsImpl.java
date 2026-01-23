package com.github.mohrezal.springbootblogrestapi.domains.storage.services.storageutils;

import com.github.mohrezal.springbootblogrestapi.shared.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageUtilsImpl implements StorageUtils {

    private final ApplicationProperties applicationProperties;

    @Override
    public boolean isValidFileExtension(MultipartFile file) {
        return applicationProperties.storage().allowedExtensions().contains(file.getContentType());
    }

    @Override
    public boolean isMaxFileSizeExceeded(long size) {
        return size > applicationProperties.storage().maxFileSize().toBytes();
    }
}

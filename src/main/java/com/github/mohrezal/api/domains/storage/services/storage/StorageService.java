package com.github.mohrezal.api.domains.storage.services.storage;

import com.github.mohrezal.api.domains.storage.enums.StorageType;
import com.github.mohrezal.api.domains.storage.models.Storage;
import com.github.mohrezal.api.domains.users.models.User;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    Storage upload(MultipartFile file, String title, String alt, StorageType type, User uploader);

    void delete(Storage storage);
}

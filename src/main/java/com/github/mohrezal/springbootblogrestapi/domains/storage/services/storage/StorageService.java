package com.github.mohrezal.springbootblogrestapi.domains.storage.services.storage;

import com.github.mohrezal.springbootblogrestapi.domains.storage.enums.StorageType;
import com.github.mohrezal.springbootblogrestapi.domains.storage.models.Storage;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    Storage upload(MultipartFile file, String title, String alt, StorageType type, User uploader);

    void delete(Storage storage);
}

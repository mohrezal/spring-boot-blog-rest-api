package com.github.mohrezal.api.domains.storage.services.storage;

import com.github.mohrezal.api.domains.storage.enums.StorageType;
import com.github.mohrezal.api.domains.storage.exceptions.context.StorageUploadExceptionContext;
import com.github.mohrezal.api.domains.storage.exceptions.types.StorageUploadFailedException;
import com.github.mohrezal.api.domains.storage.models.Storage;
import com.github.mohrezal.api.domains.storage.repositories.StorageRepository;
import com.github.mohrezal.api.domains.storage.services.s3.S3StorageService;
import com.github.mohrezal.api.domains.storage.services.storageutils.StorageUtilsService;
import com.github.mohrezal.api.domains.users.models.User;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final StorageUtilsService storageUtilsService;
    private final S3StorageService s3StorageService;
    private final StorageRepository storageRepository;

    @Override
    public Storage upload(
            MultipartFile file, String title, String alt, StorageType type, User uploader) {
        var userId = uploader.getId();
        var storageType = type != null ? type.name() : null;
        var context =
                new StorageUploadExceptionContext(
                        userId, file.getOriginalFilename(), file.getSize(), storageType);
        try {
            String mimeType = storageUtilsService.getMimeType(file);
            String filename = storageUtilsService.generateFilename(file.getOriginalFilename());

            s3StorageService.upload(file, filename, mimeType);

            Storage storage =
                    Storage.builder()
                            .filename(filename)
                            .originalFilename(file.getOriginalFilename())
                            .mimeType(mimeType)
                            .size(file.getSize())
                            .title(title)
                            .alt(alt)
                            .type(type)
                            .user(uploader)
                            .build();

            return storageRepository.save(storage);
        } catch (IOException e) {
            throw new StorageUploadFailedException(context, e);
        }
    }

    @Override
    public void delete(Storage storage) {
        s3StorageService.delete(storage.getFilename());
        storageRepository.delete(storage);
    }
}

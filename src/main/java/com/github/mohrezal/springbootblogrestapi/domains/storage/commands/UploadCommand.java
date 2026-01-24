package com.github.mohrezal.springbootblogrestapi.domains.storage.commands;

import com.github.mohrezal.springbootblogrestapi.domains.storage.commands.params.UploadCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.storage.dtos.StorageSummary;
import com.github.mohrezal.springbootblogrestapi.domains.storage.dtos.UploadRequest;
import com.github.mohrezal.springbootblogrestapi.domains.storage.exceptions.types.StorageFileSizeExceededException;
import com.github.mohrezal.springbootblogrestapi.domains.storage.exceptions.types.StorageInvalidMimeTypeException;
import com.github.mohrezal.springbootblogrestapi.domains.storage.exceptions.types.StorageUploadFailedException;
import com.github.mohrezal.springbootblogrestapi.domains.storage.models.Storage;
import com.github.mohrezal.springbootblogrestapi.domains.storage.repositories.StorageRepository;
import com.github.mohrezal.springbootblogrestapi.domains.storage.services.s3.S3StorageService;
import com.github.mohrezal.springbootblogrestapi.domains.storage.services.storageutils.StorageUtilsService;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Command;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Slf4j
public class UploadCommand implements Command<UploadCommandParams, StorageSummary> {

    private final StorageUtilsService storageUtils;
    private final S3StorageService s3StorageService;
    private final StorageRepository storageRepository;

    @Override
    public void validate(UploadCommandParams params) {
        try {
            if (!storageUtils.isValidMimeType(params.getUploadRequest().getFile())) {
                throw new StorageInvalidMimeTypeException();
            }
        } catch (IOException e) {
            throw new StorageInvalidMimeTypeException();
        }

        if (storageUtils.isMaxFileSizeExceeded(params.getUploadRequest().getFile().getSize())) {
            throw new StorageFileSizeExceededException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public StorageSummary execute(UploadCommandParams params) {
        User user = (User) params.getUserDetails();
        UploadRequest request = params.getUploadRequest();
        MultipartFile file = request.getFile();

        try {
            String mimeType = storageUtils.getMimeType(file);
            String filename = storageUtils.generateFilename(file.getOriginalFilename());

            s3StorageService.upload(file, filename, mimeType);

            Storage storage =
                    Storage.builder()
                            .filename(filename)
                            .originalFilename(file.getOriginalFilename())
                            .mimeType(mimeType)
                            .size(file.getSize())
                            .title(request.getTitle())
                            .alt(request.getAlt())
                            .user(user)
                            .build();

            Storage savedStorage = storageRepository.save(storage);

            return StorageSummary.builder()
                    .id(savedStorage.getId())
                    .filename(savedStorage.getFilename())
                    .originalFilename(savedStorage.getOriginalFilename())
                    .mimeType(savedStorage.getMimeType())
                    .size(savedStorage.getSize())
                    .title(savedStorage.getTitle())
                    .alt(savedStorage.getAlt())
                    .createdAt(savedStorage.getCreatedAt())
                    .build();

        } catch (IOException e) {
            log.error("Failed to upload file", e);
            throw new StorageUploadFailedException();
        }
    }
}

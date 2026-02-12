package com.github.mohrezal.api.domains.storage.commands;

import com.github.mohrezal.api.domains.storage.commands.params.UploadCommandParams;
import com.github.mohrezal.api.domains.storage.dtos.StorageSummary;
import com.github.mohrezal.api.domains.storage.enums.StorageType;
import com.github.mohrezal.api.domains.storage.exceptions.types.StorageFileSizeExceededException;
import com.github.mohrezal.api.domains.storage.exceptions.types.StorageInvalidMimeTypeException;
import com.github.mohrezal.api.domains.storage.mappers.StorageMapper;
import com.github.mohrezal.api.domains.storage.services.storage.StorageService;
import com.github.mohrezal.api.domains.storage.services.storageutils.StorageUtilsService;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedCommand;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Slf4j
public class UploadCommand extends AuthenticatedCommand<UploadCommandParams, StorageSummary> {

    private final StorageUtilsService storageUtils;
    private final StorageService storageService;
    private final StorageMapper storageMapper;

    @Override
    public void validate(UploadCommandParams params) {
        super.validate(params);

        try {
            if (!storageUtils.isValidMimeType(params.uploadRequest().file())) {
                throw new StorageInvalidMimeTypeException();
            }
        } catch (IOException e) {
            throw new StorageInvalidMimeTypeException();
        }

        if (storageUtils.isMaxFileSizeExceeded(params.uploadRequest().file().getSize())) {
            throw new StorageFileSizeExceededException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public StorageSummary execute(UploadCommandParams params) {
        try {
            validate(params);

            var request = params.uploadRequest();
            var type = params.type() != null ? params.type() : StorageType.MEDIA;

            var savedStorage =
                    storageService.upload(
                            request.file(), request.title(), request.alt(), type, user);
            log.info(
                    "Storage upload successful - type: {}, filename: {}",
                    type,
                    savedStorage.getFilename());

            return storageMapper.toStorageSummary(savedStorage);
        } catch (StorageInvalidMimeTypeException
                | StorageFileSizeExceededException
                | AccessDeniedException ex) {
            log.warn("Storage upload failed - message: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error during storage upload operation", ex);
            throw ex;
        }
    }
}

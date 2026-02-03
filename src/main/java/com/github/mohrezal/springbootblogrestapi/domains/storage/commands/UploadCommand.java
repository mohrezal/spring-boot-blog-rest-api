package com.github.mohrezal.springbootblogrestapi.domains.storage.commands;

import com.github.mohrezal.springbootblogrestapi.domains.storage.commands.params.UploadCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.storage.dtos.StorageSummary;
import com.github.mohrezal.springbootblogrestapi.domains.storage.dtos.UploadRequest;
import com.github.mohrezal.springbootblogrestapi.domains.storage.enums.StorageType;
import com.github.mohrezal.springbootblogrestapi.domains.storage.exceptions.types.StorageFileSizeExceededException;
import com.github.mohrezal.springbootblogrestapi.domains.storage.exceptions.types.StorageInvalidMimeTypeException;
import com.github.mohrezal.springbootblogrestapi.domains.storage.mappers.StorageMapper;
import com.github.mohrezal.springbootblogrestapi.domains.storage.models.Storage;
import com.github.mohrezal.springbootblogrestapi.domains.storage.services.storage.StorageService;
import com.github.mohrezal.springbootblogrestapi.domains.storage.services.storageutils.StorageUtilsService;
import com.github.mohrezal.springbootblogrestapi.shared.abstracts.AuthenticatedCommand;
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
            if (!storageUtils.isValidMimeType(params.uploadRequest().getFile())) {
                throw new StorageInvalidMimeTypeException();
            }
        } catch (IOException e) {
            throw new StorageInvalidMimeTypeException();
        }

        if (storageUtils.isMaxFileSizeExceeded(params.uploadRequest().getFile().getSize())) {
            throw new StorageFileSizeExceededException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public StorageSummary execute(UploadCommandParams params) {
        validate(params);

        UploadRequest request = params.uploadRequest();
        StorageType type = params.type() != null ? params.type() : StorageType.MEDIA;

        Storage savedStorage =
                storageService.upload(
                        request.getFile(), request.getTitle(), request.getAlt(), type, user);

        return storageMapper.toStorageSummary(savedStorage);
    }
}

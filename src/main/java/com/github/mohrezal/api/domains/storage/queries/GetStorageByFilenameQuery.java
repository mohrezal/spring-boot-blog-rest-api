package com.github.mohrezal.api.domains.storage.queries;

import com.github.mohrezal.api.domains.storage.dtos.StorageFileResponse;
import com.github.mohrezal.api.domains.storage.exceptions.context.StorageGetByFilenameExceptionContext;
import com.github.mohrezal.api.domains.storage.queries.params.GetStorageByFilenameQueryParams;
import com.github.mohrezal.api.domains.storage.repositories.StorageRepository;
import com.github.mohrezal.api.domains.storage.services.s3.S3StorageService;
import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.types.ResourceNotFoundException;
import com.github.mohrezal.api.shared.interfaces.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetStorageByFilenameQuery
        implements Query<GetStorageByFilenameQueryParams, StorageFileResponse> {

    private final StorageRepository storageRepository;
    private final S3StorageService s3StorageService;

    @Transactional(readOnly = true)
    @Override
    public StorageFileResponse execute(GetStorageByFilenameQueryParams params) {
        var context = new StorageGetByFilenameExceptionContext(params.filename());
        var storage =
                storageRepository
                        .findByFilename(params.filename())
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                MessageKey.SHARED_ERROR_RESOURCE_NOT_FOUND,
                                                context));

        var data = s3StorageService.download(storage.getFilename());
        log.info("Get storage by filename query successful.");

        return new StorageFileResponse(data, storage.getMimeType(), storage.getFilename());
    }
}

package com.github.mohrezal.api.domains.storage.queries;

import com.github.mohrezal.api.domains.storage.dtos.StorageFileResponse;
import com.github.mohrezal.api.domains.storage.queries.params.GetStorageByFilenameQueryParams;
import com.github.mohrezal.api.domains.storage.repositories.StorageRepository;
import com.github.mohrezal.api.domains.storage.services.s3.S3StorageService;
import com.github.mohrezal.api.shared.exceptions.types.ResourceNotFoundException;
import com.github.mohrezal.api.shared.interfaces.Query;
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
public class GetStorageByFilenameQuery
        implements Query<GetStorageByFilenameQueryParams, StorageFileResponse> {

    private final StorageRepository storageRepository;
    private final S3StorageService s3StorageService;

    @Transactional(readOnly = true)
    @Override
    public StorageFileResponse execute(GetStorageByFilenameQueryParams params) {
        var storage =
                storageRepository
                        .findByFilename(params.filename())
                        .orElseThrow(ResourceNotFoundException::new);

        var data = s3StorageService.download(storage.getFilename());

        return StorageFileResponse.builder()
                .data(data)
                .mimeType(storage.getMimeType())
                .filename(storage.getFilename())
                .build();
    }
}

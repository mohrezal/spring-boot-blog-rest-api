package com.github.mohrezal.api.domains.storage.queries;

import com.github.mohrezal.api.domains.storage.dtos.StorageSummary;
import com.github.mohrezal.api.domains.storage.enums.StorageType;
import com.github.mohrezal.api.domains.storage.mappers.StorageMapper;
import com.github.mohrezal.api.domains.storage.queries.params.GetUserStorageListQueryParams;
import com.github.mohrezal.api.domains.storage.repositories.StorageRepository;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedQuery;
import com.github.mohrezal.api.shared.dtos.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class GetUserStorageListQuery
        extends AuthenticatedQuery<GetUserStorageListQueryParams, PageResponse<StorageSummary>> {

    private final StorageRepository storageRepository;
    private final StorageMapper storageMapper;

    @Transactional(readOnly = true)
    @Override
    public PageResponse<StorageSummary> execute(GetUserStorageListQueryParams params) {
        var currentUser = getCurrentUser(params);

        var pageable =
                PageRequest.of(
                        params.page(), params.size(), Sort.by(Sort.Direction.DESC, "createdAt"));
        var storageList =
                storageRepository.findAllByUserAndType(currentUser, StorageType.MEDIA, pageable);

        var response = PageResponse.from(storageList, storageMapper::toStorageSummary);
        log.info("Get user storage list query successful.");
        return response;
    }
}

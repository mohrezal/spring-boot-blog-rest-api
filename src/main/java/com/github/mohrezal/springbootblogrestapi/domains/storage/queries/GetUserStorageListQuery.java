package com.github.mohrezal.springbootblogrestapi.domains.storage.queries;

import com.github.mohrezal.springbootblogrestapi.domains.storage.dtos.StorageSummary;
import com.github.mohrezal.springbootblogrestapi.domains.storage.enums.StorageType;
import com.github.mohrezal.springbootblogrestapi.domains.storage.mappers.StorageMapper;
import com.github.mohrezal.springbootblogrestapi.domains.storage.models.Storage;
import com.github.mohrezal.springbootblogrestapi.domains.storage.queries.params.GetUserStorageListQueryParams;
import com.github.mohrezal.springbootblogrestapi.domains.storage.repositories.StorageRepository;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.shared.dtos.PageResponse;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Slf4j
@Service
public class GetUserStorageListQuery
        implements Query<GetUserStorageListQueryParams, PageResponse<StorageSummary>> {

    private final StorageRepository storageRepository;
    private final StorageMapper storageMapper;

    @Transactional(readOnly = true)
    @Override
    public PageResponse<StorageSummary> execute(GetUserStorageListQueryParams params) {
        User user = (User) params.getUserDetails();
        Pageable pageable =
                PageRequest.of(
                        params.getPage(),
                        params.getSize(),
                        Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<@NonNull Storage> storageList =
                storageRepository.findAllByUserAndType(user, StorageType.MEDIA, pageable);

        return PageResponse.from(storageList, storageMapper::toStorageSummary);
    }
}

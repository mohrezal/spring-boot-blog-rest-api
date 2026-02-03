package com.github.mohrezal.api.domains.storage.mappers;

import com.github.mohrezal.api.domains.storage.dtos.StorageSummary;
import com.github.mohrezal.api.domains.storage.models.Storage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StorageMapper {

    StorageSummary toStorageSummary(Storage storage);
}

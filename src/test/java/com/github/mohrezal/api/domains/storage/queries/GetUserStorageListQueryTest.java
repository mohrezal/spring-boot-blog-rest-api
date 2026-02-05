package com.github.mohrezal.api.domains.storage.queries;

import static com.github.mohrezal.api.support.builders.StorageBuilder.aStorage;
import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.storage.dtos.StorageSummary;
import com.github.mohrezal.api.domains.storage.enums.StorageType;
import com.github.mohrezal.api.domains.storage.mappers.StorageMapper;
import com.github.mohrezal.api.domains.storage.models.Storage;
import com.github.mohrezal.api.domains.storage.queries.params.GetUserStorageListQueryParams;
import com.github.mohrezal.api.domains.storage.repositories.StorageRepository;
import com.github.mohrezal.api.domains.users.models.User;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class GetUserStorageListQueryTest {

    @Mock private StorageRepository storageRepository;

    @Mock private StorageMapper storageMapper;

    @InjectMocks private GetUserStorageListQuery query;

    private final User mockedUser = aUser().build();
    private final Storage mockedStorage = aStorage().build();
    private final StorageSummary storageSummary =
            new StorageSummary(
                    mockedStorage.getId(),
                    mockedStorage.getFilename(),
                    mockedStorage.getOriginalFilename(),
                    mockedStorage.getMimeType(),
                    mockedStorage.getSize(),
                    mockedStorage.getTitle(),
                    mockedStorage.getAlt(),
                    mockedStorage.getCreatedAt(),
                    mockedStorage.getUpdatedAt());

    private final Page<@NonNull Storage> mockedPage =
            new PageImpl<>(List.of(mockedStorage), PageRequest.of(0, 10), 1);

    private final GetUserStorageListQueryParams params =
            new GetUserStorageListQueryParams(mockedUser, 0, 10);

    @Test
    void execute_whenGivenValidParams_shouldReturnPaginatedStorageSummary() {
        when(storageRepository.findAllByUserAndType(
                        eq(mockedUser), eq(StorageType.MEDIA), any(Pageable.class)))
                .thenReturn(mockedPage);

        when(storageMapper.toStorageSummary(mockedStorage)).thenReturn(storageSummary);

        var response = query.execute(params);

        verify(storageRepository, times(1))
                .findAllByUserAndType(eq(mockedUser), eq(StorageType.MEDIA), any(Pageable.class));

        verify(storageMapper, times(1)).toStorageSummary(mockedStorage);

        assertNotNull(response);
        assertEquals(response.getItems().size(), mockedPage.getContent().size());
    }

    @Test
    void execute_whenNoStorageFound_shouldReturnEmptyPage() {
        var emptyPage = Page.<Storage>empty();

        when(storageRepository.findAllByUserAndType(
                        eq(mockedUser), eq(StorageType.MEDIA), any(Pageable.class)))
                .thenReturn(emptyPage);

        var response = query.execute(params);

        verify(storageRepository, times(1))
                .findAllByUserAndType(eq(mockedUser), eq(StorageType.MEDIA), any(Pageable.class));

        assertTrue(response.getItems().isEmpty());
        assertEquals(0, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
    }
}

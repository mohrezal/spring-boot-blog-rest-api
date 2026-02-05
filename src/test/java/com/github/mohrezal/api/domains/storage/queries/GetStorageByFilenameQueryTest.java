package com.github.mohrezal.api.domains.storage.queries;

import static com.github.mohrezal.api.support.builders.StorageBuilder.aStorage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.storage.models.Storage;
import com.github.mohrezal.api.domains.storage.queries.params.GetStorageByFilenameQueryParams;
import com.github.mohrezal.api.domains.storage.repositories.StorageRepository;
import com.github.mohrezal.api.domains.storage.services.s3.S3StorageService;
import com.github.mohrezal.api.shared.exceptions.types.ResourceNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetStorageByFilenameQueryTest {

    @Mock private StorageRepository storageRepository;

    @Mock private S3StorageService s3StorageService;

    @InjectMocks private GetStorageByFilenameQuery query;

    private final GetStorageByFilenameQueryParams params =
            new GetStorageByFilenameQueryParams("profile.jpg");

    private final Storage mockedStorage = aStorage().withFilename(params.filename()).build();

    @Test
    void execute_whenGivenValidFilename_shouldReturnStorageFileResponse() {
        when(storageRepository.findByFilename(params.filename()))
                .thenReturn(Optional.ofNullable(mockedStorage));
        when(s3StorageService.download(params.filename())).thenReturn(new byte[0]);

        var response = query.execute(params);

        verify(storageRepository, times(1)).findByFilename(eq(params.filename()));
        verify(s3StorageService, times(1)).download(eq(params.filename()));

        assertNotNull(response);
        assertEquals(params.filename(), response.getFilename());
    }

    @Test
    void execute_whenGivenInvalidFilename_shouldThrowResourceNotFoundException() {
        when(storageRepository.findByFilename(params.filename()))
                .thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> query.execute(params));

        verify(storageRepository, times(1)).findByFilename(eq(params.filename()));
        verify(s3StorageService, times(0)).download(eq(params.filename()));
    }
}

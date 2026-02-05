package com.github.mohrezal.api.domains.storage.commands;

import static com.github.mohrezal.api.support.builders.StorageBuilder.aStorage;
import static com.github.mohrezal.api.support.builders.StorageSummaryBuilder.aStorageSummary;
import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.storage.commands.params.UploadCommandParams;
import com.github.mohrezal.api.domains.storage.dtos.StorageSummary;
import com.github.mohrezal.api.domains.storage.dtos.UploadRequest;
import com.github.mohrezal.api.domains.storage.enums.StorageType;
import com.github.mohrezal.api.domains.storage.exceptions.types.StorageFileSizeExceededException;
import com.github.mohrezal.api.domains.storage.exceptions.types.StorageInvalidMimeTypeException;
import com.github.mohrezal.api.domains.storage.mappers.StorageMapper;
import com.github.mohrezal.api.domains.storage.models.Storage;
import com.github.mohrezal.api.domains.storage.services.storage.StorageService;
import com.github.mohrezal.api.domains.storage.services.storageutils.StorageUtilsService;
import com.github.mohrezal.api.domains.users.models.User;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class UploadCommandTest {

    @Mock private StorageUtilsService storageUtils;

    @Mock private StorageService storageService;

    @Mock private StorageMapper storageMapper;

    @InjectMocks private UploadCommand command;

    @Mock private MultipartFile file;

    private final User user = aUser().build();
    private final Storage storage = aStorage().build();
    private final StorageSummary summary = aStorageSummary(storage).build();

    @Test
    void validate_whenMimeTypeIsInvalid_shouldThrowException() throws IOException {
        var request = new UploadRequest(file, "title", "alt");
        var params = new UploadCommandParams(user, request, StorageType.MEDIA);

        when(storageUtils.isValidMimeType(file)).thenReturn(false);

        assertThrows(StorageInvalidMimeTypeException.class, () -> command.execute(params));

        verify(storageUtils).isValidMimeType(file);
        verify(storageUtils, never()).isMaxFileSizeExceeded(anyLong());
        verifyNoInteractions(storageService);
        verifyNoInteractions(storageMapper);
    }

    @Test
    void validate_whenFileSizeExceeded_shouldThrowException() throws IOException {
        var request = new UploadRequest(file, "title", "alt");
        var params = new UploadCommandParams(user, request, StorageType.MEDIA);

        when(storageUtils.isValidMimeType(file)).thenReturn(true);
        when(file.getSize()).thenReturn(10_000L);
        when(storageUtils.isMaxFileSizeExceeded(10_000L)).thenReturn(true);

        assertThrows(StorageFileSizeExceededException.class, () -> command.execute(params));

        verify(storageUtils).isValidMimeType(file);
        verify(storageUtils).isMaxFileSizeExceeded(10_000L);
        verifyNoInteractions(storageService);
        verifyNoInteractions(storageMapper);
    }

    @Test
    void execute_whenTypeProvided_shouldUploadWithGivenType() throws IOException {
        var request = new UploadRequest(file, "title", "alt");
        var params = new UploadCommandParams(user, request, StorageType.MEDIA);

        when(storageUtils.isValidMimeType(file)).thenReturn(true);
        when(file.getSize()).thenReturn(1024L);
        when(storageUtils.isMaxFileSizeExceeded(1024L)).thenReturn(false);

        when(storageService.upload(
                        eq(file),
                        eq(params.uploadRequest().title()),
                        eq(params.uploadRequest().alt()),
                        eq(StorageType.MEDIA),
                        eq(user)))
                .thenReturn(storage);

        when(storageMapper.toStorageSummary(storage)).thenReturn(summary);

        var result = command.execute(params);

        assertNotNull(result);
        assertEquals(summary, result);

        verify(storageService)
                .upload(
                        file,
                        params.uploadRequest().title(),
                        params.uploadRequest().alt(),
                        StorageType.MEDIA,
                        user);
        verify(storageMapper).toStorageSummary(storage);
    }
}

package com.github.mohrezal.api.domains.storage.commands;

import static com.github.mohrezal.api.support.builders.StorageBuilder.aStorage;
import static com.github.mohrezal.api.support.builders.StorageSummaryBuilder.aStorageSummary;
import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.storage.commands.params.UploadProfileCommandParams;
import com.github.mohrezal.api.domains.storage.dtos.StorageSummary;
import com.github.mohrezal.api.domains.storage.dtos.UploadProfileRequest;
import com.github.mohrezal.api.domains.storage.enums.StorageType;
import com.github.mohrezal.api.domains.storage.exceptions.types.StorageFileSizeExceededException;
import com.github.mohrezal.api.domains.storage.exceptions.types.StorageInvalidMimeTypeException;
import com.github.mohrezal.api.domains.storage.mappers.StorageMapper;
import com.github.mohrezal.api.domains.storage.models.Storage;
import com.github.mohrezal.api.domains.storage.services.storage.StorageService;
import com.github.mohrezal.api.domains.storage.services.storageutils.StorageUtilsService;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class UploadProfileCommandTest {

    @Mock private StorageService storageService;

    @Mock private StorageUtilsService storageUtilsService;

    @Mock private StorageMapper storageMapper;

    @Mock private UserRepository userRepository;

    @InjectMocks private UploadProfileCommand command;

    @Mock private MultipartFile file;

    private final User mockedUser = aUser().build();
    private final Storage mockedStorage = aStorage().build();
    private final StorageSummary storageSummary = aStorageSummary(mockedStorage).build();

    @Test
    void validate_whenMimeTypeIsInvalid_shouldThrowException() throws IOException {
        var request = new UploadProfileRequest(file);
        var params = new UploadProfileCommandParams(mockedUser, request);

        when(storageUtilsService.isValidMimeType(file)).thenReturn(false);

        assertThrows(StorageInvalidMimeTypeException.class, () -> command.execute(params));

        verify(storageMapper, times(0)).toStorageSummary(any());
        verify(storageUtilsService, times(1)).isValidMimeType(any());
        verify(storageUtilsService, times(0)).isMaxFileSizeExceeded(anyLong());
        verify(userRepository, times(0)).delete(any());
        verify(storageService, times(0)).upload(any(), any(), any(), any(), any());
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void validate_whenMaxFileSizeExceeded_shouldThrownException() throws IOException {
        var request = new UploadProfileRequest(file);
        var params = new UploadProfileCommandParams(mockedUser, request);

        when(storageUtilsService.isValidMimeType(file)).thenReturn(true);

        when(storageUtilsService.isMaxFileSizeExceeded(file.getSize())).thenReturn(true);

        assertThrows(StorageFileSizeExceededException.class, () -> command.execute(params));

        verify(storageMapper, times(0)).toStorageSummary(any());
        verify(storageUtilsService, times(1)).isValidMimeType(any());
        verify(storageUtilsService, times(1)).isMaxFileSizeExceeded(anyLong());
        verify(userRepository, times(0)).delete(any());
        verify(storageService, times(0)).upload(any(), any(), any(), any(), any());
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void execute_whenUserHasNoAvatar_shouldUploadAndSaveProfile() throws IOException {
        var request = new UploadProfileRequest(file);
        var params = new UploadProfileCommandParams(mockedUser, request);
        mockedUser.setAvatar(null);

        when(storageUtilsService.isValidMimeType(file)).thenReturn(true);
        when(file.getSize()).thenReturn(mockedStorage.getSize());
        when(storageUtilsService.isMaxFileSizeExceeded(mockedStorage.getSize())).thenReturn(false);

        when(storageService.upload(
                        eq(params.uploadProfileRequest().file()),
                        anyString(),
                        anyString(),
                        eq(StorageType.PROFILE),
                        eq(mockedUser)))
                .thenReturn(mockedStorage);

        when(userRepository.save(mockedUser)).thenReturn(mockedUser);
        when(storageMapper.toStorageSummary(mockedStorage)).thenReturn(storageSummary);

        var response = command.execute(params);

        verify(storageMapper, times(1)).toStorageSummary(any());
        verify(storageUtilsService, times(1)).isValidMimeType(any());
        verify(storageUtilsService, times(1)).isMaxFileSizeExceeded(anyLong());
        verify(userRepository, times(0)).delete(any());
        verify(storageService, times(1)).upload(any(), any(), any(), any(), any());
        verify(userRepository, times(1)).save(any());

        assertNotNull(response);
        assertEquals(response.filename(), mockedStorage.getFilename());
    }

    @Test
    void execute_whenUserHasAvatar_shouldUploadAndSaveProfile() throws IOException {
        var request = new UploadProfileRequest(file);
        var params = new UploadProfileCommandParams(mockedUser, request);

        var newStorage = aStorage().build();
        var storageSummary = aStorageSummary(newStorage).build();

        mockedUser.setAvatar(mockedStorage);

        when(storageUtilsService.isValidMimeType(file)).thenReturn(true);
        when(file.getSize()).thenReturn(mockedStorage.getSize());
        when(storageUtilsService.isMaxFileSizeExceeded(mockedStorage.getSize())).thenReturn(false);

        when(storageService.upload(
                        eq(file),
                        anyString(),
                        anyString(),
                        eq(StorageType.PROFILE),
                        eq(mockedUser)))
                .thenReturn(newStorage);

        when(storageMapper.toStorageSummary(newStorage)).thenReturn(storageSummary);

        when(userRepository.save(mockedUser)).thenReturn(mockedUser);

        var response = command.execute(params);

        assertNotNull(response);
        assertEquals(newStorage.getFilename(), response.filename());

        verify(storageUtilsService, times(1)).isValidMimeType(file);
        verify(storageUtilsService, times(1)).isMaxFileSizeExceeded(mockedStorage.getSize());

        verify(storageService, times(1)).delete(mockedStorage);

        verify(storageService, times(1)).upload(any(), any(), any(), any(), any());
        verify(userRepository, times(1)).save(mockedUser);
        verify(storageMapper, times(1)).toStorageSummary(newStorage);
    }
}

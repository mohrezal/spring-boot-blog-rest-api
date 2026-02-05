package com.github.mohrezal.api.domains.storage.commands;

import static com.github.mohrezal.api.support.builders.StorageBuilder.aStorage;
import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.storage.commands.params.DeleteCommandParams;
import com.github.mohrezal.api.domains.storage.models.Storage;
import com.github.mohrezal.api.domains.storage.repositories.StorageRepository;
import com.github.mohrezal.api.domains.storage.services.storage.StorageService;
import com.github.mohrezal.api.domains.storage.services.storageutils.StorageUtilsService;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.services.userutils.UserUtilsService;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import com.github.mohrezal.api.shared.exceptions.types.ResourceNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteCommandTest {

    @Mock private StorageRepository storageRepository;

    @Mock private UserUtilsService userUtilsService;

    @Mock private StorageUtilsService storageUtilsService;

    @Mock private StorageService storageService;

    @InjectMocks private DeleteCommand command;

    private final User user = aUser().build();
    private final Storage storage = aStorage().build();

    @Test
    void execute_whenStorageNotFound_shouldThrowException() {
        var params = new DeleteCommandParams(user, "profile.jpg");
        when(storageRepository.findByFilename(eq(params.fileName()))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> command.execute(params));
        verify(storageRepository).findByFilename(params.fileName());
        verifyNoInteractions(storageService);
    }

    @Test
    void execute_whenUserIsNotOwnerAndNotAdmin_shouldThrowAccessDenied() {
        var params = new DeleteCommandParams(user, storage.getFilename());

        when(storageRepository.findByFilename(storage.getFilename()))
                .thenReturn(Optional.of(storage));

        when(storageUtilsService.isOwner(user, storage)).thenReturn(false);

        when(userUtilsService.isAdmin(user)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> command.execute(params));

        verify(storageService, never()).delete(any());
    }

    @Test
    void execute_whenUserIsOwner_shouldDeleteStorage() {
        var params = new DeleteCommandParams(user, storage.getFilename());

        when(storageRepository.findByFilename(storage.getFilename()))
                .thenReturn(Optional.of(storage));

        when(storageUtilsService.isOwner(user, storage)).thenReturn(true);

        command.execute(params);

        verify(storageService).delete(storage);
    }

    @Test
    void execute_whenUserIsAdmin_shouldDeleteStorage() {
        var params = new DeleteCommandParams(user, storage.getFilename());

        when(storageRepository.findByFilename(storage.getFilename()))
                .thenReturn(Optional.of(storage));

        when(storageUtilsService.isOwner(user, storage)).thenReturn(false);

        when(userUtilsService.isAdmin(user)).thenReturn(true);

        command.execute(params);

        verify(storageService).delete(storage);
    }
}

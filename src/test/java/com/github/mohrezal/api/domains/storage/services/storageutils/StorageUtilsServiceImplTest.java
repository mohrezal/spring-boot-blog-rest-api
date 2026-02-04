package com.github.mohrezal.api.domains.storage.services.storageutils;

import static com.github.mohrezal.api.support.builders.StorageBuilder.aStorage;
import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.storage.models.Storage;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.shared.config.ApplicationProperties;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.unit.DataSize;

@ExtendWith(MockitoExtension.class)
class StorageUtilsServiceImplTest {

    @InjectMocks private StorageUtilsServiceImpl storageUtilsService;

    @Mock private ApplicationProperties applicationProperties;

    @Mock private ApplicationProperties.Storage storage;

    @Mock private DataSize dataSize;

    @Test
    void getExtension_whenInvalidFilename_shouldReturnEmpty() {
        assertEquals("", storageUtilsService.getExtension(null));
        assertEquals("", storageUtilsService.getExtension("filename"));
    }

    @Test
    void getExtension_whenValidFilename_shouldReturnFormattedFilename() {
        assertEquals("jpg", storageUtilsService.getExtension("profile.jpg"));
    }

    @Test
    void getExtension_whenMultipleDots_shouldReturnLastExtension() {
        assertEquals("jpg", storageUtilsService.getExtension("my.file.name.jpg"));
    }

    @Test
    void generateFilename_whenExtensionIsBlank_shouldReturnRandomUUID() {
        assertDoesNotThrow(() -> UUID.fromString(storageUtilsService.generateFilename(null)));
    }

    @Test
    void generateFilename_whenHasExtension_shouldReturnUuidWithExtension() {
        String result = storageUtilsService.generateFilename("profile.jpg");
        assertTrue(result.endsWith(".jpg"));
        assertDoesNotThrow(() -> UUID.fromString(result.split("\\.")[0]));
    }

    @Test
    void isMaxFileSizeExceeded_whenGivenSizeIsLargerThanMaximumFileSize_shouldReturnTrue() {
        when(applicationProperties.storage()).thenReturn(storage);
        when(storage.maxFileSize()).thenReturn(dataSize);
        when(dataSize.toBytes()).thenReturn(1000L);

        assertTrue(storageUtilsService.isMaxFileSizeExceeded(1200L));
    }

    @Test
    void isMaxFileSizeExceeded_whenGivenSizeIsSmallerThanMaximumFileSize_shouldReturnFalse() {
        when(applicationProperties.storage()).thenReturn(storage);
        when(storage.maxFileSize()).thenReturn(dataSize);
        when(dataSize.toBytes()).thenReturn(1000L);

        assertFalse(storageUtilsService.isMaxFileSizeExceeded(900L));
    }

    @Test
    void isOwner_whenUserOwnsStorage_shouldReturnTrue() {
        UUID userId = UUID.randomUUID();
        User user = aUser().withId(userId).build();
        Storage storage = aStorage().withUser(user).build();

        assertTrue(storageUtilsService.isOwner(user, storage));
    }

    @Test
    void isOwner_whenUserDoesNotOwnStorage_shouldReturnFalse() {
        User owner = aUser().withId(UUID.randomUUID()).build();
        User otherUser = aUser().withId(UUID.randomUUID()).build();
        Storage storage = aStorage().withUser(owner).build();

        assertFalse(storageUtilsService.isOwner(otherUser, storage));
    }
}

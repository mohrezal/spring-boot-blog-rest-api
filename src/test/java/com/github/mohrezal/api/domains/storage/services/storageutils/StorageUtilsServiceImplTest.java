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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class StorageUtilsServiceImplTest {

    @Mock private MultipartFile multipartFile;

    private StorageUtilsServiceImpl storageUtilsService;

    @BeforeEach
    void setUp() {
        storageUtilsService =
                createService(List.of("text/plain", "image/jpeg"), DataSize.ofBytes(1000L));
    }

    private static StorageUtilsServiceImpl createService(
            List<String> allowedMimeTypes, DataSize maxFileSize) {
        ApplicationProperties.Storage storageProperties =
                new ApplicationProperties.Storage(
                        allowedMimeTypes,
                        maxFileSize,
                        "endpoint",
                        "accessKey",
                        "secretKey",
                        "bucket",
                        "region");
        ApplicationProperties applicationProperties =
                new ApplicationProperties(null, storageProperties, null, null);
        return new StorageUtilsServiceImpl(applicationProperties);
    }

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
        assertTrue(storageUtilsService.isMaxFileSizeExceeded(1200L));
    }

    @Test
    void isMaxFileSizeExceeded_whenGivenSizeIsSmallerThanMaximumFileSize_shouldReturnFalse() {
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

    @Test
    void getMimeType_whenValidFile_shouldReturnDetectedMimeType() throws IOException {
        byte[] content = "plain text content".getBytes(StandardCharsets.UTF_8);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(content));
        when(multipartFile.getOriginalFilename()).thenReturn("test.txt");

        String mimeType = storageUtilsService.getMimeType(multipartFile);

        assertEquals("text/plain", mimeType);
    }

    @Test
    void isValidMimeType_whenMimeTypeIsAllowed_shouldReturnTrue() throws IOException {
        byte[] content = "plain text content".getBytes(StandardCharsets.UTF_8);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(content));
        when(multipartFile.getOriginalFilename()).thenReturn("test.txt");

        assertTrue(storageUtilsService.isValidMimeType(multipartFile));
    }

    @Test
    void isValidMimeType_whenMimeTypeIsNotAllowed_shouldReturnFalse() throws IOException {
        byte[] content = "plain text content".getBytes(StandardCharsets.UTF_8);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(content));
        when(multipartFile.getOriginalFilename()).thenReturn("test.txt");

        StorageUtilsServiceImpl service =
                createService(List.of("image/jpeg", "image/png"), DataSize.ofBytes(1000L));

        assertFalse(service.isValidMimeType(multipartFile));
    }
}

package com.github.mohrezal.api.domains.storage.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mohrezal.api.config.Routes;
import com.github.mohrezal.api.domains.storage.enums.StorageType;
import com.github.mohrezal.api.domains.storage.models.Storage;
import com.github.mohrezal.api.domains.storage.repositories.StorageRepository;
import com.github.mohrezal.api.domains.storage.services.s3.S3StorageService;
import com.github.mohrezal.api.domains.users.enums.UserRole;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.services.ratelimit.RateLimitService;
import com.github.mohrezal.api.support.builders.UserBuilder;
import com.github.mohrezal.api.support.security.AuthenticationUtils;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class StorageControllerTest {

    private static final byte[] JPEG_BYTES =
            new byte[] {
                (byte) 0xFF,
                (byte) 0xD8,
                (byte) 0xFF,
                (byte) 0xE0,
                0x00,
                0x10,
                0x4A,
                0x46,
                0x49,
                0x46,
                0x00,
                0x01
            };

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired StorageRepository storageRepository;
    @MockitoBean S3StorageService s3StorageService;
    @MockitoBean RateLimitService rateLimitService;

    User testUser;
    User adminUser;
    User otherUser;

    MockMultipartFile validImageFile;
    MockMultipartFile invalidMimeTypeFile;
    MockMultipartFile oversizedFile;

    @BeforeEach
    void setUp() throws IOException {
        testUser =
                userRepository.save(
                        UserBuilder.aUser()
                                .withEmail("test@example.com")
                                .withHandle("testuser")
                                .withRole(UserRole.USER)
                                .build());

        adminUser =
                userRepository.save(
                        UserBuilder.aUser()
                                .withEmail("admin@example.com")
                                .withHandle("adminuser")
                                .withRole(UserRole.ADMIN)
                                .build());

        otherUser =
                userRepository.save(
                        UserBuilder.aUser()
                                .withEmail("other@example.com")
                                .withHandle("otheruser")
                                .withRole(UserRole.USER)
                                .build());

        validImageFile = new MockMultipartFile("file", "image.jpg", "image/jpeg", JPEG_BYTES);

        invalidMimeTypeFile =
                new MockMultipartFile(
                        "file", "document.pdf", "application/pdf", "fake-pdf-content".getBytes());

        oversizedFile =
                new MockMultipartFile("file", "large.jpg", "image/jpeg", new byte[6 * 1024 * 1024]);

        when(s3StorageService.upload(any(), anyString(), anyString())).thenReturn("mocked-s3-key");
        when(s3StorageService.download(anyString())).thenReturn(JPEG_BYTES);
        doNothing().when(s3StorageService).delete(anyString());

        when(rateLimitService.tryConsume(anyString(), any()))
                .thenReturn(new RateLimitService.ConsumptionResult(true, 100L, 0L));
    }

    @Test
    void upload_whenValidFile_shouldPersistToDatabase() throws Exception {
        mockMvc.perform(
                        multipart(Routes.build(Routes.Storage.BASE))
                                .file(validImageFile)
                                .param("title", "Test Image")
                                .param("alt", "Alt text")
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.filename").exists())
                .andExpect(jsonPath("$.originalFilename").value("image.jpg"))
                .andExpect(jsonPath("$.mimeType").value("image/jpeg"))
                .andExpect(jsonPath("$.title").value("Test Image"))
                .andExpect(jsonPath("$.alt").value("Alt text"));

        List<Storage> storages = storageRepository.findAll();
        assertThat(storages).hasSize(1);

        Storage savedStorage = storages.getFirst();
        assertThat(savedStorage.getFilename()).isNotBlank();
        assertThat(savedStorage.getOriginalFilename()).isEqualTo("image.jpg");
        assertThat(savedStorage.getMimeType()).isEqualTo("image/jpeg");
        assertThat(savedStorage.getTitle()).isEqualTo("Test Image");
        assertThat(savedStorage.getAlt()).isEqualTo("Alt text");
        assertThat(savedStorage.getType()).isEqualTo(StorageType.MEDIA);
        assertThat(savedStorage.getUser().getId()).isEqualTo(testUser.getId());

        verify(s3StorageService, times(1)).upload(any(), anyString(), anyString());
    }

    @Test
    void upload_whenInvalidMimeType_shouldReturn400() throws Exception {
        mockMvc.perform(
                        multipart(Routes.build(Routes.Storage.BASE))
                                .file(invalidMimeTypeFile)
                                .param("title", "Test PDF")
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(testUser)))
                .andExpect(status().isBadRequest());

        assertThat(storageRepository.findAll()).isEmpty();
        verify(s3StorageService, times(0)).upload(any(), anyString(), anyString());
    }

    @Test
    void upload_whenOversizedFile_shouldReturn400() throws Exception {
        mockMvc.perform(
                        multipart(Routes.build(Routes.Storage.BASE))
                                .file(oversizedFile)
                                .param("title", "Large Image")
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(testUser)))
                .andExpect(status().isBadRequest());

        assertThat(storageRepository.findAll()).isEmpty();
    }

    @Test
    void upload_whenMissingFile_shouldReturn400() throws Exception {
        mockMvc.perform(
                        multipart(Routes.build(Routes.Storage.BASE))
                                .param("title", "Test Image")
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(testUser)))
                .andExpect(status().isBadRequest());

        assertThat(storageRepository.findAll()).isEmpty();
    }

    @Test
    void upload_whenNotAuthenticated_shouldReturn401() throws Exception {
        mockMvc.perform(
                        multipart(Routes.build(Routes.Storage.BASE))
                                .file(validImageFile)
                                .param("title", "Test Image")
                                .with(csrf()))
                .andExpect(status().isUnauthorized());

        assertThat(storageRepository.findAll()).isEmpty();
    }

    @Test
    void upload_whenValidAuthentication_shouldSucceed() throws Exception {
        mockMvc.perform(
                        multipart(Routes.build(Routes.Storage.BASE))
                                .file(validImageFile)
                                .param("title", "User Upload")
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(testUser)))
                .andExpect(status().isCreated());

        List<Storage> storages = storageRepository.findAll();
        assertThat(storages).hasSize(1);
        assertThat(storages.getFirst().getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void download_whenExistingFile_shouldReturnFileBytes() throws Exception {
        Storage storage = createStorageRecord("test-file.jpg", testUser);

        mockMvc.perform(get(buildStorageRoute(storage.getFilename())))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"))
                .andExpect(content().bytes(JPEG_BYTES));

        verify(s3StorageService, times(1)).download(storage.getFilename());
    }

    @Test
    void download_whenNonExistentFile_shouldReturn404() throws Exception {
        mockMvc.perform(get(buildStorageRoute("non-existent.jpg")))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_whenOwner_shouldDeleteFromDatabaseAndS3() throws Exception {
        Storage storage = createStorageRecord("test-file.jpg", testUser);

        mockMvc.perform(
                        delete(buildStorageRoute(storage.getFilename()))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(testUser)))
                .andExpect(status().isNoContent());

        assertThat(storageRepository.findAll()).isEmpty();
        verify(s3StorageService, times(1)).delete(storage.getFilename());
    }

    @Test
    void delete_whenNonOwner_shouldReturn403() throws Exception {
        Storage storage = createStorageRecord("test-file.jpg", testUser);

        mockMvc.perform(
                        delete(buildStorageRoute(storage.getFilename()))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(otherUser)))
                .andExpect(status().isForbidden());

        assertThat(storageRepository.findAll()).hasSize(1);
    }

    @Test
    void delete_whenAdmin_shouldSucceedRegardlessOfOwner() throws Exception {
        Storage storage = createStorageRecord("test-file.jpg", testUser);

        mockMvc.perform(
                        delete(buildStorageRoute(storage.getFilename()))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(adminUser)))
                .andExpect(status().isNoContent());

        assertThat(storageRepository.findAll()).isEmpty();
    }

    @Test
    void delete_whenNotAuthenticated_shouldReturn401() throws Exception {
        Storage storage = createStorageRecord("test-file.jpg", testUser);

        mockMvc.perform(delete(buildStorageRoute(storage.getFilename())).with(csrf()))
                .andExpect(status().isUnauthorized());

        assertThat(storageRepository.findAll()).hasSize(1);
    }

    @Test
    void delete_whenNonExistentFile_shouldReturn404() throws Exception {
        mockMvc.perform(
                        delete(buildStorageRoute("non-existent.jpg"))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void list_whenAuthenticated_shouldReturnUserFilesOnly() throws Exception {
        createMultipleStorageRecords(3, testUser, "test-file-");
        createMultipleStorageRecords(2, otherUser, "other-file-");

        mockMvc.perform(
                        get(Routes.build(Routes.Storage.BASE, Routes.Storage.LIST))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(3))
                .andExpect(jsonPath("$.totalElements").value(3));
    }

    @Test
    void list_whenPagination_shouldReturnCorrectPage() throws Exception {
        createMultipleStorageRecords(15, testUser, "test-file-");

        mockMvc.perform(
                        get(Routes.build(Routes.Storage.BASE, Routes.Storage.LIST))
                                .param("page", "0")
                                .param("size", "10")
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(10))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.pageNumber").value(0));

        mockMvc.perform(
                        get(Routes.build(Routes.Storage.BASE, Routes.Storage.LIST))
                                .param("page", "1")
                                .param("size", "10")
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(5))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.pageNumber").value(1));
    }

    @Test
    void list_whenNotAuthenticated_shouldReturn401() throws Exception {
        mockMvc.perform(get(Routes.build(Routes.Storage.BASE, Routes.Storage.LIST)).with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void uploadProfile_whenValidFile_shouldReplaceExistingProfile() throws Exception {
        MvcResult result1 =
                mockMvc.perform(
                                multipart(Routes.build(Routes.Storage.BASE, Routes.Storage.PROFILE))
                                        .file(validImageFile)
                                        .with(csrf())
                                        .with(AuthenticationUtils.authenticate(testUser)))
                        .andExpect(status().isOk())
                        .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        String firstFilename =
                mapper.readTree(result1.getResponse().getContentAsString())
                        .get("filename")
                        .asText();

        List<Storage> storages = storageRepository.findAll();
        assertThat(storages).hasSize(1);
        assertThat(storages.getFirst().getType()).isEqualTo(StorageType.PROFILE);
        assertThat(storages.getFirst().getFilename()).isEqualTo(firstFilename);

        MockMultipartFile newProfileFile =
                new MockMultipartFile("file", "new-profile.jpg", "image/jpeg", JPEG_BYTES);

        mockMvc.perform(
                        multipart(Routes.build(Routes.Storage.BASE, Routes.Storage.PROFILE))
                                .file(newProfileFile)
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(testUser)))
                .andExpect(status().isOk());

        verify(s3StorageService, times(1)).delete(firstFilename);

        storages = storageRepository.findAll();
        assertThat(storages).hasSize(1);
        assertThat(storages.getFirst().getType()).isEqualTo(StorageType.PROFILE);
        assertThat(storages.getFirst().getFilename()).isNotEqualTo(firstFilename);
    }

    @Test
    void uploadProfile_whenInvalidMimeType_shouldReturn400() throws Exception {
        mockMvc.perform(
                        multipart(Routes.build(Routes.Storage.BASE, Routes.Storage.PROFILE))
                                .file(invalidMimeTypeFile)
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(testUser)))
                .andExpect(status().isBadRequest());

        assertThat(storageRepository.findAll()).isEmpty();
    }

    @Test
    void upload_whenS3UploadFails_shouldRollbackDatabase() throws Exception {
        when(s3StorageService.upload(any(), anyString(), anyString()))
                .thenThrow(new RuntimeException("S3 upload failed"));

        mockMvc.perform(
                        multipart(Routes.build(Routes.Storage.BASE))
                                .file(validImageFile)
                                .param("title", "Test Image")
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(testUser)))
                .andExpect(status().isInternalServerError());

        assertThat(storageRepository.findAll()).isEmpty();
    }

    @Test
    void delete_whenS3DeleteFails_shouldNotDeleteFromDatabase() throws Exception {
        Storage storage = createStorageRecord("test-file.jpg", testUser);

        doThrow(new RuntimeException("S3 delete failed"))
                .when(s3StorageService)
                .delete(anyString());

        mockMvc.perform(
                        delete(buildStorageRoute(storage.getFilename()))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(testUser)))
                .andExpect(status().isInternalServerError());

        assertThat(storageRepository.findAll()).hasSize(1);
    }

    private Storage createStorageRecord(String filename, User user) {
        Storage storage =
                Storage.builder()
                        .filename(filename)
                        .originalFilename("image.jpg")
                        .mimeType("image/jpeg")
                        .size(100L)
                        .type(StorageType.MEDIA)
                        .user(user)
                        .build();
        return storageRepository.save(storage);
    }

    private void createMultipleStorageRecords(int count, User user, String filenamePrefix) {
        for (int i = 0; i < count; i++) {
            Storage storage =
                    Storage.builder()
                            .filename(filenamePrefix + i + ".jpg")
                            .originalFilename("image-" + i + ".jpg")
                            .mimeType("image/jpeg")
                            .size(100L)
                            .type(StorageType.MEDIA)
                            .user(user)
                            .build();
            storageRepository.save(storage);
        }
    }

    private String buildStorageRoute(String filename) {
        return Routes.build(
                Routes.Storage.BASE, Routes.Storage.BY_FILENAME.replace("{filename}", filename));
    }
}

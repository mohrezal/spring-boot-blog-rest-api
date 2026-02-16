package com.github.mohrezal.api.domains.storage.commands;

import com.github.mohrezal.api.domains.storage.commands.params.UploadProfileCommandParams;
import com.github.mohrezal.api.domains.storage.dtos.StorageSummary;
import com.github.mohrezal.api.domains.storage.enums.StorageType;
import com.github.mohrezal.api.domains.storage.exceptions.context.StorageUploadExceptionContext;
import com.github.mohrezal.api.domains.storage.exceptions.types.StorageFileSizeExceededException;
import com.github.mohrezal.api.domains.storage.exceptions.types.StorageInvalidMimeTypeException;
import com.github.mohrezal.api.domains.storage.mappers.StorageMapper;
import com.github.mohrezal.api.domains.storage.services.storage.StorageService;
import com.github.mohrezal.api.domains.storage.services.storageutils.StorageUtilsService;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedCommand;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadProfileCommand
        extends AuthenticatedCommand<UploadProfileCommandParams, StorageSummary> {

    private final StorageService storageService;
    private final StorageUtilsService storageUtilsService;
    private final StorageMapper storageMapper;
    private final UserRepository userRepository;

    private void assertProfileUploadConstraints(
            UploadProfileCommandParams params, UUID currentUserId) {
        MultipartFile file = params.uploadProfileRequest().file();
        var context =
                new StorageUploadExceptionContext(
                        currentUserId,
                        file.getOriginalFilename(),
                        file.getSize(),
                        StorageType.PROFILE.name());
        try {
            if (!storageUtilsService.isValidMimeType(file)) {
                throw new StorageInvalidMimeTypeException(context);
            }
        } catch (IOException e) {
            throw new StorageInvalidMimeTypeException(context, e);
        }

        if (storageUtilsService.isMaxFileSizeExceeded(file.getSize())) {
            throw new StorageFileSizeExceededException(context);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public StorageSummary execute(UploadProfileCommandParams params) {
        var currentUser = getCurrentUser(params);
        assertProfileUploadConstraints(params, currentUser.getId());

        if (currentUser.getAvatar() != null) {
            storageService.delete(currentUser.getAvatar());
        }

        var firstName = currentUser.getFirstName() != null ? currentUser.getFirstName() : "";
        var lastName = currentUser.getLastName() != null ? currentUser.getLastName() : "";
        var profileInfo = String.format("%s %s profile image", firstName, lastName).trim();

        var storage =
                storageService.upload(
                        params.uploadProfileRequest().file(),
                        profileInfo,
                        profileInfo,
                        StorageType.PROFILE,
                        currentUser);

        currentUser.setAvatar(storage);
        userRepository.save(currentUser);

        log.info("Profile image upload successful - filename: {}", storage.getFilename());

        return storageMapper.toStorageSummary(storage);
    }
}

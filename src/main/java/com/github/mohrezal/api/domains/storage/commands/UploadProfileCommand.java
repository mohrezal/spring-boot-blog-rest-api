package com.github.mohrezal.api.domains.storage.commands;

import com.github.mohrezal.api.domains.storage.commands.params.UploadProfileCommandParams;
import com.github.mohrezal.api.domains.storage.dtos.StorageSummary;
import com.github.mohrezal.api.domains.storage.enums.StorageType;
import com.github.mohrezal.api.domains.storage.exceptions.types.StorageFileSizeExceededException;
import com.github.mohrezal.api.domains.storage.exceptions.types.StorageInvalidMimeTypeException;
import com.github.mohrezal.api.domains.storage.mappers.StorageMapper;
import com.github.mohrezal.api.domains.storage.services.storage.StorageService;
import com.github.mohrezal.api.domains.storage.services.storageutils.StorageUtilsService;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedCommand;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Slf4j
public class UploadProfileCommand
        extends AuthenticatedCommand<UploadProfileCommandParams, StorageSummary> {

    private final StorageService storageService;
    private final StorageUtilsService storageUtilsService;
    private final StorageMapper storageMapper;
    private final UserRepository userRepository;

    @Override
    public void validate(UploadProfileCommandParams params) {
        super.validate(params);

        MultipartFile file = params.uploadProfileRequest().file();
        try {
            if (!storageUtilsService.isValidMimeType(file)) {
                throw new StorageInvalidMimeTypeException();
            }
        } catch (IOException e) {
            throw new StorageInvalidMimeTypeException();
        }

        if (storageUtilsService.isMaxFileSizeExceeded(file.getSize())) {
            throw new StorageFileSizeExceededException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public StorageSummary execute(UploadProfileCommandParams params) {
        validate(params);

        try {
            if (user.getAvatar() != null) {
                storageService.delete(user.getAvatar());
            }

            var firstName = user.getFirstName() != null ? user.getFirstName() : "";
            var lastName = user.getLastName() != null ? user.getLastName() : "";
            var profileInfo = String.format("%s %s profile image", firstName, lastName).trim();

            var storage =
                    storageService.upload(
                            params.uploadProfileRequest().file(),
                            profileInfo,
                            profileInfo,
                            StorageType.PROFILE,
                            user);

            user.setAvatar(storage);
            userRepository.save(user);

            log.info("Profile image upload successful - filename: {}", storage.getFilename());

            return storageMapper.toStorageSummary(storage);
        } catch (StorageInvalidMimeTypeException
                | StorageFileSizeExceededException
                | AccessDeniedException ex) {
            log.warn("Profile image upload failed - message: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error during profile image upload operation", ex);
            throw ex;
        }
    }
}

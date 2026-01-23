package com.github.mohrezal.springbootblogrestapi.domains.storage.commands;

import com.github.mohrezal.springbootblogrestapi.domains.storage.commands.params.UploadCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.storage.dtos.UploadResponse;
import com.github.mohrezal.springbootblogrestapi.domains.storage.exceptions.types.StorageFileSizeExceededException;
import com.github.mohrezal.springbootblogrestapi.domains.storage.exceptions.types.StorageInvalidFileExtensionException;
import com.github.mohrezal.springbootblogrestapi.domains.storage.services.storageutils.StorageUtils;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Slf4j
public class UploadCommand implements Command<UploadCommandParams, UploadResponse> {

    private final StorageUtils storageUtils;

    @Override
    public void validate(UploadCommandParams params) {
        if (!storageUtils.isValidFileExtension(params.getUploadRequest().getFile())) {
            throw new StorageInvalidFileExtensionException();
        }
        if (storageUtils.isMaxFileSizeExceeded(params.getUploadRequest().getFile().getSize())) {
            throw new StorageFileSizeExceededException();
        }
    }

    @Override
    public UploadResponse execute(UploadCommandParams params) {
        User user = (User) params.getUserDetails();

        return null;
    }
}

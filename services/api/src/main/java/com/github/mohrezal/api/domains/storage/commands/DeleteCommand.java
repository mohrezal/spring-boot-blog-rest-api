package com.github.mohrezal.api.domains.storage.commands;

import com.github.mohrezal.api.domains.storage.commands.params.DeleteCommandParams;
import com.github.mohrezal.api.domains.storage.exceptions.context.StorageDeleteExceptionContext;
import com.github.mohrezal.api.domains.storage.repositories.StorageRepository;
import com.github.mohrezal.api.domains.storage.services.storage.StorageService;
import com.github.mohrezal.api.domains.storage.services.storageutils.StorageUtilsService;
import com.github.mohrezal.api.domains.users.services.userutils.UserUtilsService;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedCommand;
import com.github.mohrezal.api.shared.enums.MessageKey;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import com.github.mohrezal.api.shared.exceptions.types.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteCommand extends AuthenticatedCommand<DeleteCommandParams, Void> {

    private final StorageRepository storageRepository;
    private final UserUtilsService userUtilsService;
    private final StorageUtilsService storageUtilsService;
    private final StorageService storageService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void execute(DeleteCommandParams params) {
        var currentUser = getCurrentUser(params);
        var fileName = params.fileName();
        var context = new StorageDeleteExceptionContext(currentUser.getId(), fileName);

        var storage =
                storageRepository
                        .findByFilename(fileName)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                MessageKey.SHARED_ERROR_RESOURCE_NOT_FOUND,
                                                context));

        if (!storageUtilsService.isOwner(currentUser, storage)
                && !userUtilsService.isAdmin(currentUser)) {
            throw new AccessDeniedException(context);
        }

        storageService.delete(storage);
        log.info("Storage delete successful - filename: {}", fileName);
        return null;
    }
}

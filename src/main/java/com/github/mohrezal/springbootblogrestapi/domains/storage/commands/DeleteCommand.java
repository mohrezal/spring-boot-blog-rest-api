package com.github.mohrezal.springbootblogrestapi.domains.storage.commands;

import com.github.mohrezal.springbootblogrestapi.domains.storage.commands.params.DeleteCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.storage.models.Storage;
import com.github.mohrezal.springbootblogrestapi.domains.storage.repositories.StorageRepository;
import com.github.mohrezal.springbootblogrestapi.domains.storage.services.storage.StorageService;
import com.github.mohrezal.springbootblogrestapi.domains.storage.services.storageutils.StorageUtilsService;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.domains.users.services.userutils.UserUtilsService;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.AccessDeniedException;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.ResourceNotFoundException;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Slf4j
public class DeleteCommand implements Command<DeleteCommandParams, Void> {

    private final StorageRepository storageRepository;
    private final UserUtilsService userUtilsService;
    private final StorageUtilsService storageUtilsService;
    private final StorageService storageService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void execute(DeleteCommandParams params) {
        User user = (User) params.getUserDetails();
        String fileName = params.getFileName();
        Storage storage =
                storageRepository
                        .findByFilename(fileName)
                        .orElseThrow(ResourceNotFoundException::new);

        if (!storageUtilsService.isOwner(user, storage) && !userUtilsService.isAdmin(user)) {
            throw new AccessDeniedException();
        }
        storageService.delete(storage);
        return null;
    }
}

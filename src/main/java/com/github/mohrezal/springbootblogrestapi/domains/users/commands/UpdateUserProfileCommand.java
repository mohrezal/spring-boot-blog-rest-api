package com.github.mohrezal.springbootblogrestapi.domains.users.commands;

import com.github.mohrezal.springbootblogrestapi.domains.users.commands.params.UpdateUserProfileCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.UpdateUserProfileRequest;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.UserSummary;
import com.github.mohrezal.springbootblogrestapi.domains.users.mappers.UserMapper;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.domains.users.repositories.UserRepository;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.ForbiddenException;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.InvalidRequestException;
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
public class UpdateUserProfileCommand
        implements Command<UpdateUserProfileCommandParams, UserSummary> {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public void validate(UpdateUserProfileCommandParams params) {
        UpdateUserProfileRequest request = params.getRequest();

        if ((request.getFirstName() != null && request.getFirstName().isBlank())
                || (request.getLastName() != null && request.getLastName().isBlank())
                || (request.getBio() != null && request.getBio().isBlank())
                || (request.getAvatarUrl() != null && request.getAvatarUrl().isBlank())) {
            throw new InvalidRequestException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserSummary execute(UpdateUserProfileCommandParams params) {
        if (!(params.getUserDetails() instanceof User currentUser)) {
            throw new ForbiddenException();
        }

        UpdateUserProfileRequest request = params.getRequest();

        if (request.getFirstName() != null) {
            currentUser.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            currentUser.setLastName(request.getLastName());
        }

        if (request.getBio() != null) {
            currentUser.setBio(request.getBio());
        }

        if (request.getAvatarUrl() != null) {
            currentUser.setAvatarUrl(request.getAvatarUrl());
        }

        User updatedUser = userRepository.save(currentUser);

        return userMapper.toUserSummary(updatedUser);
    }
}

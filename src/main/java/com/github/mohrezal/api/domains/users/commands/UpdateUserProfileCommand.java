package com.github.mohrezal.api.domains.users.commands;

import com.github.mohrezal.api.domains.users.commands.params.UpdateUserProfileCommandParams;
import com.github.mohrezal.api.domains.users.dtos.UpdateUserProfileRequest;
import com.github.mohrezal.api.domains.users.dtos.UserSummary;
import com.github.mohrezal.api.domains.users.mappers.UserMapper;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedCommand;
import com.github.mohrezal.api.shared.exceptions.types.InvalidRequestException;
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
        extends AuthenticatedCommand<UpdateUserProfileCommandParams, UserSummary> {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public void validate(UpdateUserProfileCommandParams params) {
        super.validate(params);

        UpdateUserProfileRequest request = params.request();

        if ((request.getFirstName() != null && request.getFirstName().isBlank())
                || (request.getLastName() != null && request.getLastName().isBlank())
                || (request.getBio() != null && request.getBio().isBlank())) {
            throw new InvalidRequestException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserSummary execute(UpdateUserProfileCommandParams params) {
        validate(params);

        UpdateUserProfileRequest request = params.request();

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        User updatedUser = userRepository.save(user);

        return userMapper.toUserSummary(updatedUser);
    }
}

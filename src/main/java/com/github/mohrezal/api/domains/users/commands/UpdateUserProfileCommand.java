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

        var request = params.request();

        if ((request.firstName() != null && request.firstName().isBlank())
                || (request.lastName() != null && request.lastName().isBlank())
                || (request.bio() != null && request.bio().isBlank())) {
            throw new InvalidRequestException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserSummary execute(UpdateUserProfileCommandParams params) {
        validate(params);

        UpdateUserProfileRequest request = params.request();

        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }

        if (request.bio() != null) {
            user.setBio(request.bio());
        }
        User updatedUser = userRepository.save(user);

        return userMapper.toUserSummary(updatedUser);
    }
}

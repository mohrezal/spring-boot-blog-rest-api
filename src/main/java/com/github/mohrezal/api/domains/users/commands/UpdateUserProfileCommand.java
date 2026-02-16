package com.github.mohrezal.api.domains.users.commands;

import com.github.mohrezal.api.domains.users.commands.params.UpdateUserProfileCommandParams;
import com.github.mohrezal.api.domains.users.dtos.UserSummary;
import com.github.mohrezal.api.domains.users.exceptions.context.UserUpdateProfileExceptionContext;
import com.github.mohrezal.api.domains.users.mappers.UserMapper;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedCommand;
import com.github.mohrezal.api.shared.exceptions.types.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateUserProfileCommand
        extends AuthenticatedCommand<UpdateUserProfileCommandParams, UserSummary> {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private void validateRequest(User currentUser, UpdateUserProfileCommandParams params) {
        var request = params.request();
        var context =
                new UserUpdateProfileExceptionContext(
                        currentUser.getId(),
                        request.firstName() != null,
                        request.lastName() != null,
                        request.bio() != null);

        if ((request.firstName() != null && request.firstName().isBlank())
                || (request.lastName() != null && request.lastName().isBlank())
                || (request.bio() != null && request.bio().isBlank())) {
            throw new InvalidRequestException(context);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserSummary execute(UpdateUserProfileCommandParams params) {
        var currentUser = getCurrentUser(params);
        validateRequest(currentUser, params);
        var request = params.request();

        if (request.firstName() != null) {
            currentUser.setFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            currentUser.setLastName(request.lastName());
        }

        if (request.bio() != null) {
            currentUser.setBio(request.bio());
        }

        var updatedUser = userRepository.save(currentUser);

        log.info("User profile update successful.");
        return userMapper.toUserSummary(updatedUser);
    }
}

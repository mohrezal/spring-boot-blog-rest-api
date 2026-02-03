package com.github.mohrezal.api.domains.users.commands;

import com.github.mohrezal.api.domains.users.commands.params.UnFollowUserCommandParams;
import com.github.mohrezal.api.domains.users.exceptions.types.UserCannotFollowOrUnfollowSelfException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFollowingException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.models.UserFollow;
import com.github.mohrezal.api.domains.users.repositories.UserFollowRepository;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedCommand;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Service
@Slf4j
public class UnFollowUserCommand extends AuthenticatedCommand<UnFollowUserCommandParams, Void> {

    private final UserFollowRepository userFollowRepository;
    private final UserRepository userRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void execute(UnFollowUserCommandParams params) {
        validate(params);

        User targetUser =
                userRepository
                        .findByHandle(params.handle())
                        .orElseThrow(UserNotFoundException::new);

        if (user.getId().equals(targetUser.getId())) {
            throw new UserCannotFollowOrUnfollowSelfException();
        }

        Optional<UserFollow> userFollow =
                userFollowRepository.findByFollowedAndFollower(targetUser, user);
        if (userFollow.isEmpty()) {
            throw new UserNotFollowingException();
        }

        userFollowRepository.delete(userFollow.get());

        return null;
    }
}

package com.github.mohrezal.api.domains.users.commands;

import com.github.mohrezal.api.domains.notifications.events.UserFollowedEvent;
import com.github.mohrezal.api.domains.users.commands.params.FollowUserCommandParams;
import com.github.mohrezal.api.domains.users.exceptions.context.UserFollowExceptionContext;
import com.github.mohrezal.api.domains.users.exceptions.types.UserAlreadyFollowingException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserCannotFollowOrUnfollowSelfException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.models.UserFollow;
import com.github.mohrezal.api.domains.users.repositories.UserFollowRepository;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Service
@Slf4j
public class FollowUserCommand extends AuthenticatedCommand<FollowUserCommandParams, Void> {

    private final UserFollowRepository userFollowRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void execute(FollowUserCommandParams params) {
        validate(params);

        var followerId = user.getId();
        var followerHandle = user.getHandle();
        var context =
                new UserFollowExceptionContext(
                        followerId.toString(), followerHandle, params.handle());

        log.debug(
                "Executing FollowUserCommand - follower: {}, target: {}",
                followerHandle,
                params.handle());
        var targetUser =
                userRepository
                        .findByHandle(params.handle())
                        .orElseThrow(() -> new UserNotFoundException(context));

        if (followerId.equals(targetUser.getId())) {
            throw new UserCannotFollowOrUnfollowSelfException(context);
        }

        var isUserAlreadyFollowed =
                userFollowRepository.isAlreadyFollowing(followerId, targetUser.getId());

        if (isUserAlreadyFollowed) {
            throw new UserAlreadyFollowingException(context);
        }

        var userFollow = UserFollow.builder().follower(user).followed(targetUser).build();

        userFollowRepository.save(userFollow);

        log.info(
                "User followed successfully - follower: {}, followed:{}",
                followerId,
                targetUser.getId());

        eventPublisher.publishEvent(new UserFollowedEvent(user, targetUser));

        return null;
    }
}

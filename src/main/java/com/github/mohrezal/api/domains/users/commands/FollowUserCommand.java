package com.github.mohrezal.api.domains.users.commands;

import com.github.mohrezal.api.domains.notifications.events.UserFollowedEvent;
import com.github.mohrezal.api.domains.users.commands.params.FollowUserCommandParams;
import com.github.mohrezal.api.domains.users.exceptions.types.UserAlreadyFollowingException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserCannotFollowOrUnfollowSelfException;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.models.User;
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
        log.debug(
                "Executing FollowUserCommand - follower: {}, target: {}",
                user.getHandle(),
                params.handle());
        try {
            validate(params);

            User targetUser =
                    userRepository
                            .findByHandle(params.handle())
                            .orElseThrow(UserNotFoundException::new);

            if (user.getId().equals(targetUser.getId())) {
                throw new UserCannotFollowOrUnfollowSelfException();
            }

            boolean isUserAlreadyFollowed =
                    userFollowRepository.isAlreadyFollowing(user.getId(), targetUser.getId());

            if (isUserAlreadyFollowed) {
                throw new UserAlreadyFollowingException();
            }

            UserFollow userFollow =
                    UserFollow.builder().follower(user).followed(targetUser).build();

            userFollowRepository.save(userFollow);

            log.info(
                    "User followed successfully - follower: {}, followed:{}",
                    user.getId(),
                    targetUser.getId());

            eventPublisher.publishEvent(new UserFollowedEvent(user, targetUser));

            return null;

        } catch (UserNotFoundException ex) {
            log.warn("Follow failed - target user not found: {}", params.handle());
            throw ex;
        } catch (UserAlreadyFollowingException ex) {
            log.warn(
                    "Follow failed - user attempted to follow already following: {}", user.getId());
            throw ex;
        } catch (UserCannotFollowOrUnfollowSelfException ex) {
            log.info("Follow failed - user attempted to follow to self: {}", user.getId());
            throw ex;
        } catch (Exception ex) {
            log.error(
                    "Unexpected error in FollowUserCommand - follower: {}, target: {}",
                    user.getId(),
                    params.handle(),
                    ex);
            throw ex;
        }
    }
}

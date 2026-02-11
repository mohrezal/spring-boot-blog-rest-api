package com.github.mohrezal.api.domains.users.queries;

import com.github.mohrezal.api.domains.storage.mappers.StorageMapper;
import com.github.mohrezal.api.domains.users.dtos.FollowerSummary;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.queries.params.GetUserFollowersQueryParams;
import com.github.mohrezal.api.domains.users.repositories.UserFollowRepository;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedQuery;
import com.github.mohrezal.api.shared.dtos.PageResponse;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Slf4j
public class GetUserFollowersQuery
        extends AuthenticatedQuery<GetUserFollowersQueryParams, PageResponse<FollowerSummary>> {

    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;
    private final StorageMapper storageMapper;

    @Transactional(readOnly = true)
    @Override
    public PageResponse<FollowerSummary> execute(GetUserFollowersQueryParams params) {
        validate(params);

        try {
            var targetUser =
                    userRepository
                            .findByHandle(params.handle())
                            .orElseThrow(UserNotFoundException::new);

            var pageable =
                    PageRequest.of(
                            params.page(),
                            params.size(),
                            Sort.by(Sort.Direction.DESC, "createdAt"));

            var followersPage = userFollowRepository.findByFollowedId(targetUser.getId(), pageable);

            var followerIds =
                    followersPage.getContent().stream()
                            .map(uf -> uf.getFollower().getId())
                            .collect(Collectors.toSet());

            var followedByCurrentUser =
                    followerIds.isEmpty()
                            ? Set.of()
                            : userFollowRepository.findFollowedIdsIn(user.getId(), followerIds);

            return PageResponse.from(
                    followersPage,
                    userFollow -> {
                        var follower = userFollow.getFollower();
                        var storageSummary = storageMapper.toStorageSummary(follower.getAvatar());
                        return new FollowerSummary(
                                follower.getId(),
                                follower.getHandle(),
                                follower.getFirstName(),
                                follower.getLastName(),
                                storageSummary,
                                followedByCurrentUser.contains(follower.getId()),
                                userFollow.getCreatedAt());
                    });
        } catch (UserNotFoundException ex) {
            log.warn("Get user followers query failed - message: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error during get user followers query operation", ex);
            throw ex;
        }
    }
}

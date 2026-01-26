package com.github.mohrezal.springbootblogrestapi.domains.users.queries;

import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.FollowerSummary;
import com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.UserFollow;
import com.github.mohrezal.springbootblogrestapi.domains.users.queries.params.GetUserFollowersQueryParams;
import com.github.mohrezal.springbootblogrestapi.domains.users.repositories.UserFollowRepository;
import com.github.mohrezal.springbootblogrestapi.domains.users.repositories.UserRepository;
import com.github.mohrezal.springbootblogrestapi.shared.dtos.PageResponse;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Query;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Slf4j
public class GetUserFollowersQuery
        implements Query<GetUserFollowersQueryParams, PageResponse<FollowerSummary>> {

    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;

    @Transactional(readOnly = true)
    @Override
    public PageResponse<FollowerSummary> execute(GetUserFollowersQueryParams params) {
        User targetUser =
                userRepository
                        .findByHandle(params.getHandle())
                        .orElseThrow(UserNotFoundException::new);

        User currentUser = (User) params.getUserDetails();

        Pageable pageable =
                PageRequest.of(
                        params.getPage(),
                        params.getSize(),
                        Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<@NonNull UserFollow> followersPage =
                userFollowRepository.findByFollowedId(targetUser.getId(), pageable);

        Set<UUID> followerIds =
                followersPage.getContent().stream()
                        .map(uf -> uf.getFollower().getId())
                        .collect(Collectors.toSet());

        Set<UUID> followedByCurrentUser =
                followerIds.isEmpty()
                        ? Set.of()
                        : userFollowRepository.findFollowedIdsIn(currentUser.getId(), followerIds);

        return PageResponse.from(
                followersPage,
                userFollow -> {
                    User follower = userFollow.getFollower();
                    return FollowerSummary.builder()
                            .id(follower.getId())
                            .handle(follower.getHandle())
                            .firstName(follower.getFirstName())
                            .lastName(follower.getLastName())
                            .avatarUrl(follower.getAvatarUrl())
                            .isFollowing(followedByCurrentUser.contains(follower.getId()))
                            .followedAt(userFollow.getCreatedAt())
                            .build();
                });
    }
}

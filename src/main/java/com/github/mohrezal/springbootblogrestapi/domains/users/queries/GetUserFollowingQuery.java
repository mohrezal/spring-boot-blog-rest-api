package com.github.mohrezal.springbootblogrestapi.domains.users.queries;

import com.github.mohrezal.springbootblogrestapi.domains.storage.dtos.StorageSummary;
import com.github.mohrezal.springbootblogrestapi.domains.storage.mappers.StorageMapper;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.FollowerSummary;
import com.github.mohrezal.springbootblogrestapi.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.UserFollow;
import com.github.mohrezal.springbootblogrestapi.domains.users.queries.params.GetUserFollowingQueryParams;
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
public class GetUserFollowingQuery
        implements Query<GetUserFollowingQueryParams, PageResponse<FollowerSummary>> {

    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;
    private final StorageMapper storageMapper;

    @Transactional(readOnly = true)
    @Override
    public PageResponse<FollowerSummary> execute(GetUserFollowingQueryParams params) {
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
                userFollowRepository.findByFollowerId(targetUser.getId(), pageable);

        Set<UUID> followedIds =
                followersPage.getContent().stream()
                        .map(uf -> uf.getFollowed().getId())
                        .collect(Collectors.toSet());

        Set<UUID> followedByCurrentUser =
                followedIds.isEmpty()
                        ? Set.of()
                        : userFollowRepository.findFollowedIdsIn(currentUser.getId(), followedIds);

        return PageResponse.from(
                followersPage,
                userFollow -> {
                    User followed = userFollow.getFollowed();
                    StorageSummary storageSummary =
                            storageMapper.toStorageSummary(followed.getAvatar());
                    return FollowerSummary.builder()
                            .id(followed.getId())
                            .handle(followed.getHandle())
                            .firstName(followed.getFirstName())
                            .lastName(followed.getLastName())
                            .avatar(storageSummary)
                            .isFollowing(followedByCurrentUser.contains(followed.getId()))
                            .followedAt(userFollow.getCreatedAt())
                            .build();
                });
    }
}

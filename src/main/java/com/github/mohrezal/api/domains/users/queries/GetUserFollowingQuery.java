package com.github.mohrezal.api.domains.users.queries;

import com.github.mohrezal.api.domains.storage.dtos.StorageSummary;
import com.github.mohrezal.api.domains.storage.mappers.StorageMapper;
import com.github.mohrezal.api.domains.users.dtos.FollowerSummary;
import com.github.mohrezal.api.domains.users.exceptions.context.UserGetFollowingExceptionContext;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.queries.params.GetUserFollowingQueryParams;
import com.github.mohrezal.api.domains.users.repositories.UserFollowRepository;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedQuery;
import com.github.mohrezal.api.shared.dtos.PageResponse;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class GetUserFollowingQuery
        extends AuthenticatedQuery<GetUserFollowingQueryParams, PageResponse<FollowerSummary>> {

    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;
    private final StorageMapper storageMapper;

    @Transactional(readOnly = true)
    @Override
    public PageResponse<FollowerSummary> execute(GetUserFollowingQueryParams params) {
        validate(params);

        var context =
                new UserGetFollowingExceptionContext(
                        user.getId().toString(),
                        user.getHandle(),
                        params.handle(),
                        params.page(),
                        params.size());

        var targetUser =
                userRepository
                        .findByHandle(params.handle())
                        .orElseThrow(() -> new UserNotFoundException(context));

        var pageable =
                PageRequest.of(
                        params.page(), params.size(), Sort.by(Sort.Direction.DESC, "createdAt"));

        var followersPage = userFollowRepository.findByFollowerId(targetUser.getId(), pageable);

        var followedIds =
                followersPage.getContent().stream()
                        .map(uf -> uf.getFollowed().getId())
                        .collect(Collectors.toSet());

        var followedByCurrentUser =
                followedIds.isEmpty()
                        ? Set.of()
                        : userFollowRepository.findFollowedIdsIn(user.getId(), followedIds);

        return PageResponse.from(
                followersPage,
                userFollow -> {
                    User followed = userFollow.getFollowed();
                    StorageSummary storageSummary =
                            storageMapper.toStorageSummary(followed.getAvatar());

                    return new FollowerSummary(
                            followed.getId(),
                            followed.getHandle(),
                            followed.getFirstName(),
                            followed.getLastName(),
                            storageSummary,
                            followedByCurrentUser.contains(followed.getId()),
                            userFollow.getCreatedAt());
                });
    }
}

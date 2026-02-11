package com.github.mohrezal.api.domains.users.queries;

import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.storage.dtos.StorageSummary;
import com.github.mohrezal.api.domains.storage.mappers.StorageMapper;
import com.github.mohrezal.api.domains.users.dtos.FollowerSummary;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.models.UserFollow;
import com.github.mohrezal.api.domains.users.queries.params.GetUserFollowersQueryParams;
import com.github.mohrezal.api.domains.users.repositories.UserFollowRepository;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.dtos.PageResponse;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class GetUserFollowersQueryTest {

    @Mock private UserRepository userRepository;

    @Mock private UserFollowRepository userFollowRepository;

    @Mock private StorageMapper storageMapper;

    @InjectMocks private GetUserFollowersQuery query;

    private final User user = aUser().withId(UUID.randomUUID()).withHandle("current").build();

    @Test
    void execute_whenValidRequest_shouldReturnFollowersPage() {
        var targetUser = aUser().withId(UUID.randomUUID()).withHandle("target").build();

        var follower = aUser().withId(UUID.randomUUID()).withHandle("follower1").build();

        var userFollow = UserFollow.builder().follower(follower).followed(targetUser).build();

        var page = new PageImpl<>(List.of(userFollow), PageRequest.of(0, 10), 1);

        var params = new GetUserFollowersQueryParams(user, "target", 0, 10);

        when(userRepository.findByHandle("target")).thenReturn(Optional.of(targetUser));
        when(userFollowRepository.findByFollowedId(eq(targetUser.getId()), any(Pageable.class)))
                .thenReturn(page);
        when(userFollowRepository.findFollowedIdsIn(eq(user.getId()), anySet()))
                .thenReturn(Set.of(follower.getId()));
        when(storageMapper.toStorageSummary(any())).thenReturn(mock(StorageSummary.class));

        PageResponse<FollowerSummary> result = query.execute(params);

        assertNotNull(result);
        assertEquals(1, result.items().size());
        assertTrue(result.items().getFirst().isFollowing());
    }

    @Test
    void execute_whenTargetUserNotFound_shouldThrowUserNotFoundException() {
        var params = new GetUserFollowersQueryParams(user, "unknown", 0, 10);

        when(userRepository.findByHandle("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> query.execute(params));

        verify(userFollowRepository, never()).findByFollowedId(any(), any());
    }

    @Test
    void execute_whenNoFollowers_shouldReturnEmptyPage() {
        var targetUser = aUser().withId(UUID.randomUUID()).withHandle("target").build();

        Page<UserFollow> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        var params = new GetUserFollowersQueryParams(user, "target", 0, 10);

        when(userRepository.findByHandle("target")).thenReturn(Optional.of(targetUser));
        when(userFollowRepository.findByFollowedId(eq(targetUser.getId()), any(Pageable.class)))
                .thenReturn(emptyPage);

        PageResponse<FollowerSummary> result = query.execute(params);

        assertNotNull(result);
        assertTrue(result.items().isEmpty());

        verify(userFollowRepository, never()).findFollowedIdsIn(any(), any());
    }
}

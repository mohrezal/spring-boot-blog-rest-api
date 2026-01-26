package com.github.mohrezal.springbootblogrestapi.domains.users.repositories;

import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.UserFollow;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFollowRepository extends JpaRepository<@NonNull UserFollow, @NonNull UUID> {

    @Query(
            "SELECT CASE WHEN COUNT(uf) > 0 THEN true ELSE false END FROM UserFollow uf WHERE"
                    + " uf.follower.id = :currentUserId AND uf.followed.id = :targetUserId")
    boolean isAlreadyFollowing(UUID currentUserId, UUID targetUserId);

    Optional<UserFollow> findByFollowedAndFollower(User followed, User follower);

    @EntityGraph(attributePaths = {"follower", "follower.avatar"})
    Page<@NonNull UserFollow> findByFollowedId(UUID followedId, Pageable pageable);

    @EntityGraph(attributePaths = {"followed", "followed.avatar"})
    Page<@NonNull UserFollow> findByFollowerId(UUID followerId, Pageable pageable);

    @Query(
            "SELECT uf.followed.id FROM UserFollow uf WHERE uf.follower.id = :followerId AND"
                    + " uf.followed.id IN :targetIds")
    Set<UUID> findFollowedIdsIn(UUID followerId, Set<UUID> targetIds);
}

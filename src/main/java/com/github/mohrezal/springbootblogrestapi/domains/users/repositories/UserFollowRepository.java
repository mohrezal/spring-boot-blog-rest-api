package com.github.mohrezal.springbootblogrestapi.domains.users.repositories;

import com.github.mohrezal.springbootblogrestapi.domains.users.models.UserFollow;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFollowRepository extends JpaRepository<@NonNull UserFollow, @NonNull UUID> {

    @Query(
            "SELECT CASE WHEN COUNT(uf) > 0 THEN true ELSE false END FROM UserFollow uf WHERE"
                    + " uf.follower.id = :currentUserId AND uf.followed.id = :targetUserId")
    boolean isAlreadyFollowing(UUID currentUserId, UUID targetUserId);
}

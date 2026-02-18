package com.github.mohrezal.api.domains.posts.repositories;

import com.github.mohrezal.api.domains.posts.models.PostView;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostViewRepository extends JpaRepository<PostView, UUID> {
    @Modifying
    @Query(
            value =
                    """
                    INSERT INTO post_views (post_id, vid_hash, user_id)
                    VALUES (:postId, :vidHash, :userId)
                    ON CONFLICT (post_id, vid_hash) DO NOTHING
                    """,
            nativeQuery = true)
    int insertIgnoreDuplicate(
            @Param("postId") UUID postId,
            @Param("vidHash") String vidHash,
            @Param("userId") UUID userId);
}

package com.github.mohrezal.api.domains.posts.repositories;

import com.github.mohrezal.api.domains.posts.models.PostView;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostViewRepository extends JpaRepository<PostView, UUID> {
    boolean existsByPostIdAndViewerHash(UUID postId, String viewerHash);
}

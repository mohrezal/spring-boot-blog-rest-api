package com.github.mohrezal.springbootblogrestapi.domains.posts.repositories;

import com.github.mohrezal.springbootblogrestapi.domains.posts.models.Post;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<@NonNull Post, @NonNull UUID> {}

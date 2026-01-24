package com.github.mohrezal.springbootblogrestapi.domains.categories.repositories;

import com.github.mohrezal.springbootblogrestapi.domains.categories.models.Category;
import java.util.Set;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<@NonNull Category, @NonNull UUID> {
    Set<Category> findAllByIdIn(Set<UUID> ids);

    boolean existsBySlug(String slug);
}

package com.github.mohrezal.api.domains.categories.repositories;

import com.github.mohrezal.api.domains.categories.dtos.CategoryChildrenInfo;
import com.github.mohrezal.api.domains.categories.models.Category;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Set<Category> findAllByIdIn(Set<UUID> ids);

    boolean existsBySlug(String slug);

    @Query(
            """
            SELECT c AS category,
                   CASE WHEN COUNT(child.id) > 0 THEN true ELSE false END AS hasChildren
            FROM Category c
            LEFT JOIN Category child ON child.parent.id = c.id
            WHERE (:parentId IS NULL AND c.parent IS NULL)
               OR (:parentId IS NOT NULL AND c.parent.id = :parentId)
            GROUP BY c.id
            """)
    List<CategoryChildrenInfo> findCategoriesWithHasChildren(@Param("parentId") UUID parentId);
}

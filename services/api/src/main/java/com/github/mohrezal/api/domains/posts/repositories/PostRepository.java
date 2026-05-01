package com.github.mohrezal.api.domains.posts.repositories;

import com.github.mohrezal.api.domains.posts.enums.PostStatus;
import com.github.mohrezal.api.domains.posts.models.Post;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository
        extends JpaRepository<@NonNull Post, @NonNull UUID>,
                JpaSpecificationExecutor<@NonNull Post> {
    @EntityGraph(value = "Post.withUserAndCategories")
    Optional<Post> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    int incrementViewCount(@Param("postId") UUID postId);

    @Query(
            value =
                    """
                    SELECT id FROM (
                        SELECT p.id, ts_rank(p.search_vector, websearch_to_tsquery('simple', :query)) AS rank
                        FROM posts p
                        WHERE p.search_vector @@ websearch_to_tsquery('simple', :query)
                          AND p.status = 'PUBLISHED'
                        UNION ALL
                        SELECT p.id, word_similarity(:query, p.title) * 0.5 AS rank
                        FROM posts p
                        WHERE word_similarity(:query, p.title) > 0.5
                          AND p.status = 'PUBLISHED'
                          AND NOT p.search_vector @@ websearch_to_tsquery('simple', :query)
                    ) AS results ORDER BY rank DESC
                    """,
            countQuery =
                    """
                    SELECT COUNT(*) FROM (
                        SELECT p.id FROM posts p
                        WHERE p.search_vector @@ websearch_to_tsquery('simple', :query)
                          AND p.status = 'PUBLISHED'
                        UNION ALL
                        SELECT p.id FROM posts p
                        WHERE word_similarity(:query, p.title) > 0.5
                          AND p.status = 'PUBLISHED'
                          AND NOT p.search_vector @@ websearch_to_tsquery('simple', :query)
                    ) AS results
                    """,
            nativeQuery = true)
    Page<UUID> findAllPostBySearchQuery(@Param("query") String query, Pageable pageable);

    @EntityGraph(value = "Post.withUserAndCategories")
    List<Post> findAllByIdIn(List<UUID> ids);

    static Specification<@NonNull Post> fetchRelationships() {
        return (root, query, criteriaBuilder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch("user", JoinType.LEFT);
                root.fetch("categories", JoinType.LEFT);
            }
            return criteriaBuilder.conjunction();
        };
    }

    static Specification<@NonNull Post> hasStatus(PostStatus status) {
        return ((root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        });
    }

    static Specification<@NonNull Post> hasAuthorIds(Set<UUID> authorIds) {
        return (root, query, criteriaBuilder) -> {
            if (authorIds == null || authorIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("user").get("id").in(authorIds);
        };
    }

    static Specification<@NonNull Post> hasCategorySlugs(Set<String> categorySlugs) {
        return (root, query, criteriaBuilder) -> {
            if (categorySlugs == null || categorySlugs.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            query.distinct(true);
            var categoryJoin = root.join("categories");
            return categoryJoin.get("slug").in(categorySlugs);
        };
    }

    static Specification<@NonNull Post> hasCategorySlug(String categorySlug) {
        return (root, query, criteriaBuilder) -> {
            if (categorySlug == null) {
                return criteriaBuilder.conjunction();
            }
            var categoryJoin = root.join("categories");
            return criteriaBuilder.equal(categoryJoin.get("slug"), categorySlug);
        };
    }

    static Specification<@NonNull Post> hasSlug(String slug) {
        return (root, query, criteriaBuilder) -> {
            if (slug == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("slug"), slug);
        };
    }
}

package com.github.mohrezal.springbootblogrestapi.domains.posts.repositories;

import com.github.mohrezal.springbootblogrestapi.domains.categories.models.Category;
import com.github.mohrezal.springbootblogrestapi.domains.posts.enums.PostStatus;
import com.github.mohrezal.springbootblogrestapi.domains.posts.models.Post;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.util.Set;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository
        extends JpaRepository<@NonNull Post, @NonNull UUID>,
                JpaSpecificationExecutor<@NonNull Post> {

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
            Join<Post, Category> categoryJoin = root.join("categories");
            return categoryJoin.get("slug").in(categorySlugs);
        };
    }

    static Specification<@NonNull Post> hasCategorySlug(String categorySlug) {
        return (root, query, criteriaBuilder) -> {
            if (categorySlug == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Post, Category> categoryJoin = root.join("categories");
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

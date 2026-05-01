package com.github.mohrezal.api.domains.posts.repositories;

import static com.github.mohrezal.api.support.builders.CategoryBuilder.aCategory;
import static com.github.mohrezal.api.support.builders.PostBuilder.aPost;
import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.mohrezal.api.config.JpaAuditingConfig;
import com.github.mohrezal.api.domains.categories.repositories.CategoryRepository;
import com.github.mohrezal.api.domains.posts.enums.PostStatus;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
public class PostRepositoryTest {

    @Autowired private PostRepository postRepository;

    @Autowired private UserRepository userRepository;

    @Autowired private CategoryRepository categoryRepository;

    @Test
    void findBySlug_whenExists_shouldReturnPost() {
        var slug = "hello-world";
        var user = userRepository.save(aUser().build());
        var post =
                postRepository.save(
                        aPost().withUser(user)
                                .withSlug(slug)
                                .withStatus(PostStatus.PUBLISHED)
                                .build());

        var result = postRepository.findBySlug(slug);

        assertTrue(result.isPresent());
        assertEquals(slug, result.get().getSlug());
    }

    @Test
    void findBySlug_whenNotExists_shouldReturnEmpty() {
        var result = postRepository.findBySlug("random-slug");
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_whenHasStatusPublished_shouldReturnOnlyPublishedPosts() {
        var user = userRepository.save(aUser().build());

        postRepository.save(
                aPost().withUser(user)
                        .withTitle("published post")
                        .withSlug("published-post")
                        .withStatus(PostStatus.PUBLISHED)
                        .build());
        postRepository.save(
                aPost().withUser(user)
                        .withSlug("drafted-post")
                        .withTitle("drafted post")
                        .withStatus(PostStatus.DRAFT)
                        .build());

        var result = postRepository.findAll(PostRepository.hasStatus(PostStatus.PUBLISHED));

        assertFalse(result.isEmpty());

        for (var post : result) {
            assertEquals(PostStatus.PUBLISHED, post.getStatus());
        }
    }

    @Test
    void findAll_whenHasAuthorIdsProvided_shouldReturnOnlyPostsByThatAuthor() {
        var authorA =
                userRepository.save(
                        aUser().withEmail("authora@gmail.com").withHandle("authorA").build());
        var authorB =
                userRepository.save(
                        aUser().withEmail("authorb@gmail.com").withHandle("authorB").build());

        postRepository.save(
                aPost().withTitle("First Post").withSlug("first-post").withUser(authorA).build());
        postRepository.save(
                aPost().withTitle("Second Post").withSlug("second-post").withUser(authorB).build());

        var result = postRepository.findAll(PostRepository.hasAuthorIds(Set.of(authorA.getId())));

        assertFalse(result.isEmpty());

        for (var post : result) {
            assertEquals(authorA.getId(), post.getUser().getId());
        }
    }

    @Test
    void findAll_whenHasStatusIsNull_shouldReturnAllPosts() {
        var user = userRepository.save(aUser().build());

        postRepository.save(
                aPost().withUser(user)
                        .withSlug("first-post")
                        .withStatus(PostStatus.PUBLISHED)
                        .build());
        postRepository.save(
                aPost().withUser(user)
                        .withSlug("second-post")
                        .withStatus(PostStatus.DRAFT)
                        .build());

        var result = postRepository.findAll(PostRepository.hasStatus(null));

        assertEquals(2, result.size());
    }

    @Test
    void existsBySlug_whenExists_shouldReturnTrue() {
        var user = userRepository.save(aUser().build());
        postRepository.save(aPost().withUser(user).withSlug("exists").build());

        assertTrue(postRepository.existsBySlug("exists"));
    }

    @Test
    void existsBySlug_whenNotExists_shouldReturnFalse() {
        assertFalse(postRepository.existsBySlug("missing"));
    }

    @Test
    void findAll_whenHasCategorySlugProvided_shouldReturnPostsWithThatCategory() {
        var user = userRepository.save(aUser().build());

        var category1 =
                categoryRepository.save(aCategory().withName("Java").withSlug("java").build());

        var category2 =
                categoryRepository.save(
                        aCategory().withName("Software").withSlug("software").build());

        var post1 =
                postRepository.save(
                        aPost().withUser(user)
                                .withSlug("java-post")
                                .withCategories(Set.of(category1))
                                .build());

        postRepository.save(
                aPost().withUser(user)
                        .withSlug("software-post")
                        .withCategories(Set.of(category2))
                        .build());

        var result = postRepository.findAll(PostRepository.hasCategorySlug("java"));

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(post1.getSlug(), result.getFirst().getSlug());
    }
}

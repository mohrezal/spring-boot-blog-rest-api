package com.github.mohrezal.api.domains.posts.controllers;

import com.github.mohrezal.api.config.Routes;
import com.github.mohrezal.api.domains.posts.commands.ArchivePostCommand;
import com.github.mohrezal.api.domains.posts.commands.CreatePostCommand;
import com.github.mohrezal.api.domains.posts.commands.DeletePostCommand;
import com.github.mohrezal.api.domains.posts.commands.PublishPostCommand;
import com.github.mohrezal.api.domains.posts.commands.UnarchivePostCommand;
import com.github.mohrezal.api.domains.posts.commands.UpdatePostCommand;
import com.github.mohrezal.api.domains.posts.commands.params.ArchivePostCommandParams;
import com.github.mohrezal.api.domains.posts.commands.params.CreatePostCommandParams;
import com.github.mohrezal.api.domains.posts.commands.params.DeletePostCommandParams;
import com.github.mohrezal.api.domains.posts.commands.params.PublishPostCommandParams;
import com.github.mohrezal.api.domains.posts.commands.params.UnarchivePostCommandParams;
import com.github.mohrezal.api.domains.posts.commands.params.UpdatePostCommandParams;
import com.github.mohrezal.api.domains.posts.dtos.CreatePostRequest;
import com.github.mohrezal.api.domains.posts.dtos.PostDetail;
import com.github.mohrezal.api.domains.posts.dtos.PostSummary;
import com.github.mohrezal.api.domains.posts.dtos.SlugAvailability;
import com.github.mohrezal.api.domains.posts.dtos.UpdatePostRequest;
import com.github.mohrezal.api.domains.posts.queries.GetPostBySlugQuery;
import com.github.mohrezal.api.domains.posts.queries.GetPostSlugAvailabilityQuery;
import com.github.mohrezal.api.domains.posts.queries.GetPostsBySearchQuery;
import com.github.mohrezal.api.domains.posts.queries.GetPostsQuery;
import com.github.mohrezal.api.domains.posts.queries.params.GetPostBySlugQueryParams;
import com.github.mohrezal.api.domains.posts.queries.params.GetPostSlugAvailabilityQueryParams;
import com.github.mohrezal.api.domains.posts.queries.params.GetPostsBySearchQueryParams;
import com.github.mohrezal.api.domains.posts.queries.params.GetPostsQueryParams;
import com.github.mohrezal.api.shared.annotations.IsAdminOrUser;
import com.github.mohrezal.api.shared.dtos.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.Post.BASE)
@RequiredArgsConstructor
@Tag(name = "Post")
public class PostController {
    private final ObjectProvider<@NonNull CreatePostCommand> createPostCommands;
    private final ObjectProvider<@NonNull UpdatePostCommand> updatePostCommands;
    private final ObjectProvider<@NonNull PublishPostCommand> publishPostCommands;
    private final ObjectProvider<@NonNull ArchivePostCommand> archivePostCommands;
    private final ObjectProvider<@NonNull UnarchivePostCommand> unarchivePostCommands;
    private final ObjectProvider<@NonNull DeletePostCommand> deletePostCommands;

    private final ObjectProvider<@NonNull GetPostsQuery> getPostsQueries;
    private final ObjectProvider<@NonNull GetPostBySlugQuery> getPostBySlugQueries;
    private final ObjectProvider<@NonNull GetPostSlugAvailabilityQuery>
            getPostSlugAvailabilityQueries;
    private final GetPostsBySearchQuery getPostsBySearchQuery;

    @GetMapping
    public ResponseEntity<@NonNull PageResponse<PostSummary>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Set<String> categorySlugs,
            @RequestParam(required = false) Set<UUID> authorIds) {
        var params =
                GetPostsQueryParams.builder()
                        .page(page)
                        .size(size)
                        .categorySlugs(categorySlugs)
                        .authorIds(authorIds)
                        .build();

        var query = getPostsQueries.getObject();
        return ResponseEntity.ok(query.execute(params));
    }

    @IsAdminOrUser
    @PostMapping
    public ResponseEntity<@NonNull PostDetail> createPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreatePostRequest createPostRequest) {
        var params =
                CreatePostCommandParams.builder()
                        .createPostRequest(createPostRequest)
                        .userDetails(userDetails)
                        .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createPostCommands.getObject().execute(params));
    }

    @IsAdminOrUser
    @PutMapping(Routes.Post.UPDATE_POST_BY_SLUG)
    public ResponseEntity<@NonNull PostDetail> updatePostBySlug(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String slug,
            @Valid @RequestBody UpdatePostRequest updatePostRequest) {

        var params =
                UpdatePostCommandParams.builder()
                        .updatePostRequest(updatePostRequest)
                        .slug(slug)
                        .userDetails(userDetails)
                        .build();
        return ResponseEntity.ok().body(updatePostCommands.getObject().execute(params));
    }

    @GetMapping(Routes.Post.GET_POST_BY_SLUG)
    public ResponseEntity<@NonNull PostDetail> getPostBySlug(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable String slug) {
        var params = new GetPostBySlugQueryParams(userDetails, slug);

        return ResponseEntity.ok().body(getPostBySlugQueries.getObject().execute(params));
    }

    @GetMapping(Routes.Post.SLUG_AVAILABILITY)
    public ResponseEntity<@NonNull SlugAvailability> getSlugAvailability(
            @RequestParam(name = "slug") String slug) {
        var params = new GetPostSlugAvailabilityQueryParams(slug);

        return ResponseEntity.ok().body(getPostSlugAvailabilityQueries.getObject().execute(params));
    }

    @PostMapping(Routes.Post.PUBLISH_POST)
    @IsAdminOrUser
    public ResponseEntity<@NonNull Void> publishPost(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable String slug) {
        var params = PublishPostCommandParams.builder().slug(slug).userDetails(userDetails).build();
        publishPostCommands.getObject().execute(params);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(Routes.Post.ARCHIVE_POST)
    @IsAdminOrUser
    public ResponseEntity<@NonNull Void> archivePost(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable String slug) {
        var params = ArchivePostCommandParams.builder().slug(slug).userDetails(userDetails).build();
        archivePostCommands.getObject().execute(params);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(Routes.Post.UNARCHIVE_POST)
    @IsAdminOrUser
    public ResponseEntity<@NonNull Void> unarchivePost(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable String slug) {
        var params =
                UnarchivePostCommandParams.builder().slug(slug).userDetails(userDetails).build();
        unarchivePostCommands.getObject().execute(params);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(Routes.Post.DELETE_BY_SLUG)
    @IsAdminOrUser
    public ResponseEntity<Void> deletePostBySlug(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable String slug) {

        var params = DeletePostCommandParams.builder().slug(slug).userDetails(userDetails).build();
        deletePostCommands.getObject().execute(params);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(Routes.Post.SEARCH)
    public ResponseEntity<@NonNull PageResponse<PostSummary>> getPostsBySearchQuery(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "query") String query) {
        var params =
                GetPostsBySearchQueryParams.builder().query(query).page(page).size(size).build();
        return ResponseEntity.ok().body(getPostsBySearchQuery.execute(params));
    }
}

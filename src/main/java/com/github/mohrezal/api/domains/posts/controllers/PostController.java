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
import com.github.mohrezal.api.shared.annotations.range.Range;
import com.github.mohrezal.api.shared.constants.CookieConstants;
import com.github.mohrezal.api.shared.dtos.PageResponse;
import com.github.mohrezal.api.shared.utils.CookieUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
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
    private final CreatePostCommand createPostCommands;
    private final UpdatePostCommand updatePostCommands;
    private final PublishPostCommand publishPostCommands;
    private final ArchivePostCommand archivePostCommands;
    private final UnarchivePostCommand unarchivePostCommands;
    private final DeletePostCommand deletePostCommands;

    private final GetPostsQuery getPostsQueries;
    private final GetPostBySlugQuery getPostBySlugQueries;
    private final GetPostSlugAvailabilityQuery getPostSlugAvailabilityQueries;
    private final GetPostsBySearchQuery getPostsBySearchQuery;

    private final CookieUtils cookieUtils;

    @GetMapping
    public ResponseEntity<@NonNull PageResponse<PostSummary>> getPosts(
            @Valid @Range(max = 1000) @RequestParam(defaultValue = "0") int page,
            @Valid @Range(min = 1, max = 20) @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Set<String> categorySlugs,
            @RequestParam(required = false) Set<UUID> authorIds) {

        var params = new GetPostsQueryParams(page, size, categorySlugs, authorIds);

        return ResponseEntity.ok(getPostsQueries.execute(params));
    }

    @IsAdminOrUser
    @PostMapping
    public ResponseEntity<@NonNull PostDetail> createPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreatePostRequest createPostRequest) {
        var params = new CreatePostCommandParams(userDetails, createPostRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createPostCommands.execute(params));
    }

    @IsAdminOrUser
    @PutMapping(Routes.Post.UPDATE_POST_BY_SLUG)
    public ResponseEntity<@NonNull PostDetail> updatePostBySlug(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String slug,
            @Valid @RequestBody UpdatePostRequest updatePostRequest) {

        var params = new UpdatePostCommandParams(userDetails, updatePostRequest, slug);
        return ResponseEntity.ok().body(updatePostCommands.execute(params));
    }

    @GetMapping(Routes.Post.GET_POST_BY_SLUG)
    public ResponseEntity<@NonNull PostDetail> getPostBySlug(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable String slug) {
        var params = new GetPostBySlugQueryParams(userDetails, slug);

        return ResponseEntity.ok().body(getPostBySlugQueries.execute(params));
    }

    @GetMapping(Routes.Post.SLUG_AVAILABILITY)
    public ResponseEntity<@NonNull SlugAvailability> getSlugAvailability(
            @RequestParam(name = "slug") String slug) {
        var params = new GetPostSlugAvailabilityQueryParams(slug);

        return ResponseEntity.ok().body(getPostSlugAvailabilityQueries.execute(params));
    }

    @PostMapping(Routes.Post.PUBLISH_POST)
    @IsAdminOrUser
    public ResponseEntity<@NonNull Void> publishPost(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable String slug) {
        var params = new PublishPostCommandParams(userDetails, slug);
        publishPostCommands.execute(params);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(Routes.Post.ARCHIVE_POST)
    @IsAdminOrUser
    public ResponseEntity<@NonNull Void> archivePost(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable String slug) {
        var params = new ArchivePostCommandParams(userDetails, slug);
        archivePostCommands.execute(params);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(Routes.Post.UNARCHIVE_POST)
    @IsAdminOrUser
    public ResponseEntity<@NonNull Void> unarchivePost(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable String slug) {
        var params = new UnarchivePostCommandParams(userDetails, slug);
        unarchivePostCommands.execute(params);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(Routes.Post.DELETE_BY_SLUG)
    @IsAdminOrUser
    public ResponseEntity<Void> deletePostBySlug(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable String slug) {

        var params = new DeletePostCommandParams(userDetails, slug);
        deletePostCommands.execute(params);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(Routes.Post.SEARCH)
    public ResponseEntity<@NonNull PageResponse<PostSummary>> getPostsBySearchQuery(
            @Valid @Range(max = 1000) @RequestParam(defaultValue = "0") int page,
            @Valid @Range(min = 1, max = 20) @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "query") String query) {
        var params = new GetPostsBySearchQueryParams(query, size, page);
        return ResponseEntity.ok().body(getPostsBySearchQuery.execute(params));
    }

    @PostMapping(Routes.Post.VIEW)
    public ResponseEntity<Boolean> view(
            @PathVariable UUID slug,
            @CookieValue(name = CookieConstants.VID, required = false) String vid) {

        return ResponseEntity.ok().build();
    }
}

package com.github.mohrezal.springbootblogrestapi.domains.posts.controllers;

import com.github.mohrezal.springbootblogrestapi.config.Routes;
import com.github.mohrezal.springbootblogrestapi.domains.posts.dtos.PostDetail;
import com.github.mohrezal.springbootblogrestapi.domains.posts.dtos.PostSummary;
import com.github.mohrezal.springbootblogrestapi.domains.posts.queries.GetPostBySlugQuery;
import com.github.mohrezal.springbootblogrestapi.domains.posts.queries.GetPostsQuery;
import com.github.mohrezal.springbootblogrestapi.domains.posts.queries.params.GetPostBySlugQueryParams;
import com.github.mohrezal.springbootblogrestapi.domains.posts.queries.params.GetPostsQueryParams;
import com.github.mohrezal.springbootblogrestapi.shared.dtos.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.Post.BASE)
@RequiredArgsConstructor
@Tag(name = "Post")
public class PostController {

    private final ObjectProvider<@NonNull GetPostsQuery> getPostsQueries;
    private final ObjectProvider<@NonNull GetPostBySlugQuery> getPostBySlugQueries;

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

    @GetMapping(Routes.Post.GET_POST_BY_SLUG)
    public ResponseEntity<@NonNull PostDetail> getPostBySlug(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable String slug) {
        var params = GetPostBySlugQueryParams.builder().slug(slug).userDetails(userDetails).build();

        return ResponseEntity.ok().body(getPostBySlugQueries.getObject().execute(params));
    }
}

package com.github.mohrezal.springbootblogrestapi.domains.posts.controllers;

import com.github.mohrezal.springbootblogrestapi.config.Routes;
import com.github.mohrezal.springbootblogrestapi.domains.posts.dtos.PostSummary;
import com.github.mohrezal.springbootblogrestapi.domains.posts.queries.GetPostsQuery;
import com.github.mohrezal.springbootblogrestapi.domains.posts.queries.params.GetPostsQueryParams;
import com.github.mohrezal.springbootblogrestapi.shared.dtos.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.Post.BASE)
@RequiredArgsConstructor
@Tag(name = "Post")
public class PostController {

    private final ObjectProvider<@NonNull GetPostsQuery> getPostsQueries;

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
}

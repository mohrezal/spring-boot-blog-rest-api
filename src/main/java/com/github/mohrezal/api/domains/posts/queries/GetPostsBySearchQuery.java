package com.github.mohrezal.api.domains.posts.queries;

import com.github.mohrezal.api.domains.posts.dtos.PostSummary;
import com.github.mohrezal.api.domains.posts.mappers.PostMapper;
import com.github.mohrezal.api.domains.posts.models.Post;
import com.github.mohrezal.api.domains.posts.queries.params.GetPostsBySearchQueryParams;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.shared.dtos.PageResponse;
import com.github.mohrezal.api.shared.interfaces.Query;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class GetPostsBySearchQuery
        implements Query<GetPostsBySearchQueryParams, PageResponse<PostSummary>> {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Transactional(readOnly = true)
    @Override
    public PageResponse<PostSummary> execute(GetPostsBySearchQueryParams params) {
        var pageable = PageRequest.of(params.page(), params.size());
        var searchResult = postRepository.findAllPostBySearchQuery(params.query(), pageable);

        var rankedIds = searchResult.getContent();
        var posts = postRepository.findAllByIdIn(rankedIds);

        var postById = posts.stream().collect(Collectors.toMap(Post::getId, post -> post));

        var orderedSummaries =
                rankedIds.stream()
                        .map(postById::get)
                        .filter(Objects::nonNull)
                        .map(postMapper::toPostSummary)
                        .toList();

        return new PageResponse<>(
                orderedSummaries,
                searchResult.getNumber(),
                searchResult.getSize(),
                searchResult.getTotalElements(),
                searchResult.getTotalPages(),
                searchResult.isFirst(),
                searchResult.isLast(),
                searchResult.isEmpty(),
                searchResult.hasNext(),
                searchResult.hasPrevious()); //
    }
}

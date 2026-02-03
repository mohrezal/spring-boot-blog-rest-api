package com.github.mohrezal.api.domains.posts.queries;

import com.github.mohrezal.api.domains.posts.dtos.PostSummary;
import com.github.mohrezal.api.domains.posts.mappers.PostMapper;
import com.github.mohrezal.api.domains.posts.models.Post;
import com.github.mohrezal.api.domains.posts.queries.params.GetPostsBySearchQueryParams;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.shared.dtos.PageResponse;
import com.github.mohrezal.api.shared.interfaces.Query;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
        PageRequest pageable = PageRequest.of(params.getPage(), params.getSize());
        Page<UUID> searchResult =
                postRepository.findAllPostBySearchQuery(params.getQuery(), pageable);

        List<UUID> rankedIds = searchResult.getContent();
        List<Post> posts = postRepository.findAllByIdIn(rankedIds);

        Map<UUID, Post> postById =
                posts.stream().collect(Collectors.toMap(Post::getId, post -> post));

        List<PostSummary> orderedSummaries =
                rankedIds.stream()
                        .map(postById::get)
                        .filter(Objects::nonNull)
                        .map(postMapper::toPostSummary)
                        .toList();

        return PageResponse.<PostSummary>builder()
                .items(orderedSummaries)
                .pageNumber(searchResult.getNumber())
                .pageSize(searchResult.getSize())
                .totalElements(searchResult.getTotalElements())
                .totalPages(searchResult.getTotalPages())
                .isFirst(searchResult.isFirst())
                .isLast(searchResult.isLast())
                .isEmpty(searchResult.isEmpty())
                .hasNext(searchResult.hasNext())
                .hasPrevious(searchResult.hasPrevious())
                .build();
    }
}

package com.github.mohrezal.api.domains.posts.queries;

import com.github.mohrezal.api.domains.posts.dtos.PostSummary;
import com.github.mohrezal.api.domains.posts.enums.PostStatus;
import com.github.mohrezal.api.domains.posts.mappers.PostMapper;
import com.github.mohrezal.api.domains.posts.models.Post;
import com.github.mohrezal.api.domains.posts.queries.params.GetPostsQueryParams;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.shared.dtos.PageResponse;
import com.github.mohrezal.api.shared.interfaces.Query;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetPostsQuery implements Query<GetPostsQueryParams, PageResponse<PostSummary>> {

    private final PostRepository repository;
    private final PostMapper postMapper;

    @Transactional(readOnly = true)
    @Override
    public PageResponse<PostSummary> execute(GetPostsQueryParams params) {
        var specification =
                Specification.where(PostRepository.hasStatus(PostStatus.PUBLISHED))
                        .and(PostRepository.hasAuthorIds(params.authorIds()))
                        .and(PostRepository.hasCategorySlugs(params.categorySlugs()));

        var pageable =
                PageRequest.of(
                        params.page(), params.size(), Sort.by(Sort.Direction.DESC, "publishedAt"));
        var pagedPosts = repository.findAll(specification, pageable);
        var pagedPostIds =
                pagedPosts.getContent().stream()
                        .map(Post::getId)
                        .collect(Collectors.toCollection(LinkedHashSet::new));

        if (pagedPostIds.isEmpty()) {
            log.info("Get posts query successful.");
            return new PageResponse<>(
                    java.util.List.of(),
                    pagedPosts.getNumber(),
                    pagedPosts.getSize(),
                    pagedPosts.getTotalElements(),
                    pagedPosts.getTotalPages(),
                    pagedPosts.isFirst(),
                    pagedPosts.isLast(),
                    pagedPosts.isEmpty(),
                    pagedPosts.hasNext(),
                    pagedPosts.hasPrevious());
        }

        var postsWithRelationshipsById =
                repository.findAllByIdIn(pagedPostIds.stream().toList()).stream()
                        .collect(Collectors.toMap(Post::getId, Function.identity()));

        var orderedPostSummaries =
                pagedPostIds.stream()
                        .map(postsWithRelationshipsById::get)
                        .filter(Objects::nonNull)
                        .map(postMapper::toPostSummary)
                        .toList();

        var response =
                new PageResponse<>(
                        orderedPostSummaries,
                        pagedPosts.getNumber(),
                        pagedPosts.getSize(),
                        pagedPosts.getTotalElements(),
                        pagedPosts.getTotalPages(),
                        pagedPosts.isFirst(),
                        pagedPosts.isLast(),
                        pagedPosts.isEmpty(),
                        pagedPosts.hasNext(),
                        pagedPosts.hasPrevious());
        log.info("Get posts query successful.");
        return response;
    }
}

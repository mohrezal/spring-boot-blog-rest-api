package com.github.mohrezal.api.domains.posts.queries;

import com.github.mohrezal.api.domains.posts.dtos.PostSummary;
import com.github.mohrezal.api.domains.posts.enums.PostStatus;
import com.github.mohrezal.api.domains.posts.mappers.PostMapper;
import com.github.mohrezal.api.domains.posts.queries.params.GetPostsQueryParams;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.shared.dtos.PageResponse;
import com.github.mohrezal.api.shared.interfaces.Query;
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
                Specification.where(PostRepository.fetchRelationships())
                        .and(PostRepository.hasStatus(PostStatus.PUBLISHED))
                        .and(PostRepository.hasAuthorIds(params.authorIds()))
                        .and(PostRepository.hasCategorySlugs(params.categorySlugs()));

        var pageable =
                PageRequest.of(
                        params.page(), params.size(), Sort.by(Sort.Direction.DESC, "publishedAt"));
        var posts = repository.findAll(specification, pageable);
        var response = PageResponse.from(posts, postMapper::toPostSummary);
        log.info("Get posts query successful.");
        return response;
    }
}

package com.github.mohrezal.api.domains.posts.queries;

import com.github.mohrezal.api.domains.posts.dtos.PostSummary;
import com.github.mohrezal.api.domains.posts.enums.PostStatus;
import com.github.mohrezal.api.domains.posts.mappers.PostMapper;
import com.github.mohrezal.api.domains.posts.models.Post;
import com.github.mohrezal.api.domains.posts.queries.params.GetPostsQueryParams;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.shared.dtos.PageResponse;
import com.github.mohrezal.api.shared.interfaces.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Slf4j
public class GetPostsQuery implements Query<GetPostsQueryParams, PageResponse<PostSummary>> {

    private final PostRepository repository;
    private final PostMapper postMapper;

    @Transactional(readOnly = true)
    @Override
    public PageResponse<PostSummary> execute(GetPostsQueryParams params) {
        Specification<@NonNull Post> specification =
                Specification.where(PostRepository.fetchRelationships())
                        .and(PostRepository.hasStatus(PostStatus.PUBLISHED))
                        .and(PostRepository.hasAuthorIds(params.getAuthorIds()))
                        .and(PostRepository.hasCategorySlugs(params.getCategorySlugs()));

        Pageable pageable =
                PageRequest.of(
                        params.getPage(),
                        params.getSize(),
                        Sort.by(Sort.Direction.DESC, "publishedAt"));
        Page<@NonNull Post> posts = repository.findAll(specification, pageable);

        return PageResponse.from(posts, postMapper::toPostSummary);
    }
}

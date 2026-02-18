package com.github.mohrezal.api.domains.posts.queries;

import static com.github.mohrezal.api.support.builders.PostBuilder.aPost;
import static com.github.mohrezal.api.support.builders.PostSummaryBuilder.aPostSummary;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.posts.mappers.PostMapper;
import com.github.mohrezal.api.domains.posts.models.Post;
import com.github.mohrezal.api.domains.posts.queries.params.GetPostsQueryParams;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import java.util.List;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class GetPostsQueryTest {

    @Mock private PostRepository repository;

    @Mock private PostMapper postMapper;

    @InjectMocks private GetPostsQuery query;

    @Test
    void execute_whenNoPostsFound_shouldReturnEmptyPageResponse() {
        var params = new GetPostsQueryParams(0, 10, Set.of(), Set.of());
        var emptyPage = Page.<Post>empty();

        when(repository.findAll(
                        argThat((Specification<@NonNull Post> spec) -> true), any(Pageable.class)))
                .thenReturn(emptyPage);

        var result = query.execute(params);
        assertTrue(result.items().isEmpty());
        assertEquals(0, result.totalElements());

        verify(repository, times(1))
                .findAll(argThat((Specification<@NonNull Post> spec) -> true), any(Pageable.class));
        verify(repository, times(1))
                .findAll(argThat((Specification<@NonNull Post> spec) -> true), any(Pageable.class));
    }

    @Test
    void execute_whenPostsFound_shouldReturnPaginatedPostSummary() {
        var params = new GetPostsQueryParams(0, 10, Set.of(), Set.of());

        var post1 = aPost().withSlug("post-1").build();
        var post2 = aPost().withSlug("post-2").build();

        var summary1 = aPostSummary().withSlug("post-1").build();
        var summary2 = aPostSummary().withSlug("post-2").build();

        var page = new PageImpl<@NonNull Post>(List.of(post1, post2));

        when(repository.findAll(
                        argThat((Specification<@NonNull Post> spec) -> true), any(Pageable.class)))
                .thenReturn(page);
        when(postMapper.toPostSummary(post1)).thenReturn(summary1);
        when(postMapper.toPostSummary(post2)).thenReturn(summary2);

        var result = query.execute(params);

        verify(repository, times(1))
                .findAll(argThat((Specification<@NonNull Post> spec) -> true), any(Pageable.class));

        assertEquals(2, result.items().size());
        assertEquals(summary1, result.items().get(0));
        assertEquals(summary2, result.items().get(1));
    }
}

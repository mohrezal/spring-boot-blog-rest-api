package com.github.mohrezal.api.domains.posts.queries;

import static com.github.mohrezal.api.support.builders.PostBuilder.aPost;
import static com.github.mohrezal.api.support.builders.PostSummaryBuilder.aPostSummary;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.posts.mappers.PostMapper;
import com.github.mohrezal.api.domains.posts.queries.params.GetPostsBySearchQueryParams;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class GetPostsBySearchQueryTest {
    @Mock private PostRepository postRepository;

    @Mock private PostMapper postMapper;

    @InjectMocks private GetPostsBySearchQuery query;

    @Test
    void execute_whenSearchReturnsNoResults_shouldReturnEmptyPageResponse() {
        var params = new GetPostsBySearchQueryParams("software", 10, 0);

        var emptyPage = Page.<UUID>empty();

        when(postRepository.findAllPostBySearchQuery(eq(params.query()), any(Pageable.class)))
                .thenReturn(emptyPage);

        when(postRepository.findAllByIdIn(List.of())).thenReturn(List.of());

        var result = query.execute(params);

        assertTrue(result.items().isEmpty());
        assertEquals(0, result.totalElements());
        assertEquals(1, result.totalPages());
    }

    @Test
    void execute_whenPostsFound_shouldReturnSummariesInSearchRankOrder() {
        var params = new GetPostsBySearchQueryParams("software", 10, 0);

        var id1 = UUID.randomUUID();
        var id2 = UUID.randomUUID();

        var searchPage = new PageImpl<>(List.of(id2, id1));

        var post1 = aPost().withId(id1).build();
        var post2 = aPost().withId(id2).build();

        var summary1 = aPostSummary().withId(id1).build();
        var summary2 = aPostSummary().withId(id2).build();

        when(postRepository.findAllPostBySearchQuery(eq(params.query()), any(Pageable.class)))
                .thenReturn(searchPage);
        when(postRepository.findAllByIdIn(List.of(id2, id1))).thenReturn(List.of(post1, post2));

        when(postMapper.toPostSummary(post1)).thenReturn(summary1);
        when(postMapper.toPostSummary(post2)).thenReturn(summary2);

        var result = query.execute(params);

        assertEquals(2, result.items().size());
        assertEquals(summary2, result.items().get(0));
        assertEquals(summary1, result.items().get(1));
    }
}

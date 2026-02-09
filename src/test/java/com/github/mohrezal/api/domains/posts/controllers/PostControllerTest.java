package com.github.mohrezal.api.domains.posts.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mohrezal.api.config.Routes;
import com.github.mohrezal.api.domains.posts.commands.ArchivePostCommand;
import com.github.mohrezal.api.domains.posts.commands.CreatePostCommand;
import com.github.mohrezal.api.domains.posts.commands.DeletePostCommand;
import com.github.mohrezal.api.domains.posts.commands.PublishPostCommand;
import com.github.mohrezal.api.domains.posts.commands.UnarchivePostCommand;
import com.github.mohrezal.api.domains.posts.commands.UpdatePostCommand;
import com.github.mohrezal.api.domains.posts.dtos.PostSummary;
import com.github.mohrezal.api.domains.posts.queries.GetPostBySlugQuery;
import com.github.mohrezal.api.domains.posts.queries.GetPostSlugAvailabilityQuery;
import com.github.mohrezal.api.domains.posts.queries.GetPostsBySearchQuery;
import com.github.mohrezal.api.domains.posts.queries.GetPostsQuery;
import com.github.mohrezal.api.domains.posts.queries.params.GetPostsQueryParams;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.dtos.PageResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(ObjectMapper.class)
class PostControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private CreatePostCommand createPostCommand;

    @MockitoBean private UpdatePostCommand updatePostCommand;

    @MockitoBean private PublishPostCommand publishPostCommand;

    @MockitoBean private ArchivePostCommand archivePostCommand;

    @MockitoBean private UnarchivePostCommand unarchivePostCommand;

    @MockitoBean private DeletePostCommand deletePostCommand;

    @MockitoBean private GetPostsQuery getPostsQuery;

    @MockitoBean private GetPostBySlugQuery getPostBySlugQuery;

    @MockitoBean private GetPostSlugAvailabilityQuery getPostSlugAvailabilityQuery;

    @MockitoBean private GetPostsBySearchQuery getPostsBySearchQuery;

    @Autowired private UserRepository userRepository;

    @Test
    void getPosts_whenNoPostsExist_shouldReturnEmptyPage() throws Exception {
        Page<PostSummary> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        when(getPostsQuery.execute(any(GetPostsQueryParams.class)))
                .thenReturn(PageResponse.from(emptyPage, post -> post));

        mockMvc.perform(get(Routes.build(Routes.Post.BASE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.isEmpty").value(true));
    }
}

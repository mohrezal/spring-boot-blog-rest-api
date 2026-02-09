package com.github.mohrezal.api.domains.posts.controllers;

import static com.github.mohrezal.api.support.builders.CategoryBuilder.aCategory;
import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mohrezal.api.config.Routes;
import com.github.mohrezal.api.domains.categories.exceptions.CategoryExceptionHandler;
import com.github.mohrezal.api.domains.categories.exceptions.types.CategoryNotFoundException;
import com.github.mohrezal.api.domains.categories.repositories.CategoryRepository;
import com.github.mohrezal.api.domains.posts.commands.ArchivePostCommand;
import com.github.mohrezal.api.domains.posts.commands.CreatePostCommand;
import com.github.mohrezal.api.domains.posts.commands.DeletePostCommand;
import com.github.mohrezal.api.domains.posts.commands.PublishPostCommand;
import com.github.mohrezal.api.domains.posts.commands.UnarchivePostCommand;
import com.github.mohrezal.api.domains.posts.commands.UpdatePostCommand;
import com.github.mohrezal.api.domains.posts.commands.params.CreatePostCommandParams;
import com.github.mohrezal.api.domains.posts.commands.params.UpdatePostCommandParams;
import com.github.mohrezal.api.domains.posts.dtos.CreatePostRequest;
import com.github.mohrezal.api.domains.posts.dtos.PostDetail;
import com.github.mohrezal.api.domains.posts.dtos.PostSummary;
import com.github.mohrezal.api.domains.posts.dtos.UpdatePostRequest;
import com.github.mohrezal.api.domains.posts.enums.PostLanguage;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostSlugAlreadyExistsException;
import com.github.mohrezal.api.domains.posts.queries.GetPostBySlugQuery;
import com.github.mohrezal.api.domains.posts.queries.GetPostSlugAvailabilityQuery;
import com.github.mohrezal.api.domains.posts.queries.GetPostsBySearchQuery;
import com.github.mohrezal.api.domains.posts.queries.GetPostsQuery;
import com.github.mohrezal.api.domains.posts.queries.params.GetPostsQueryParams;
import com.github.mohrezal.api.domains.users.enums.UserRole;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.dtos.PageResponse;
import com.github.mohrezal.api.shared.exceptions.SharedExceptionHandler;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import com.github.mohrezal.api.support.builders.UserBuilder;
import com.github.mohrezal.api.support.security.AuthenticationUtils;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import({ObjectMapper.class, CategoryExceptionHandler.class, SharedExceptionHandler.class})
class PostControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private CreatePostCommand createPostCommand;

    @MockitoBean private ObjectProvider<CreatePostCommand> createPostCommands;

    @MockitoBean private UpdatePostCommand updatePostCommand;

    @MockitoBean private ObjectProvider<UpdatePostCommand> updatePostCommands;

    @MockitoBean private PublishPostCommand publishPostCommand;

    @MockitoBean private ArchivePostCommand archivePostCommand;

    @MockitoBean private UnarchivePostCommand unarchivePostCommand;

    @MockitoBean private DeletePostCommand deletePostCommand;

    @MockitoBean private GetPostsQuery getPostsQuery;

    @MockitoBean private GetPostBySlugQuery getPostBySlugQuery;

    @MockitoBean private GetPostSlugAvailabilityQuery getPostSlugAvailabilityQuery;

    @MockitoBean private GetPostsBySearchQuery getPostsBySearchQuery;

    @Autowired private UserRepository userRepository;

    @Autowired private CategoryRepository categoryRepository;

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

    @Test
    void createPost_whenAuthenticatedAndValidRequest_shouldReturn201() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());
        var category = categoryRepository.save(aCategory().build());

        var postDetail = mock(PostDetail.class);

        when(createPostCommands.getObject()).thenReturn(createPostCommand);
        when(createPostCommand.execute(any(CreatePostCommandParams.class))).thenReturn(postDetail);

        var body =
                new CreatePostRequest(
                        "Test post title",
                        "Test post content",
                        "MOCKED_AVATAR",
                        Set.of(category.getId()),
                        "Test description",
                        PostLanguage.ENGLISH);

        mockMvc.perform(
                        post(Routes.build(Routes.Post.BASE))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    @Test
    void createPost_whenNotAuthenticated_shouldReturn401() throws Exception {
        var body =
                new CreatePostRequest(
                        "Test post title",
                        "Test post content",
                        "MOCKED_AVATAR",
                        Set.of(UUID.randomUUID()),
                        "Test description",
                        PostLanguage.ENGLISH);
        mockMvc.perform(
                        post(Routes.build(Routes.Post.BASE))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createPost_whenInvalidRequest_shouldReturn400() throws Exception {
        var user =
                userRepository.save(
                        UserBuilder.aUser()
                                .withEmail("user2@test.com")
                                .withRole(UserRole.USER)
                                .build());

        var body = new CreatePostRequest(null, null, null, null, null, null);

        mockMvc.perform(
                        post(Routes.build(Routes.Post.BASE))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPost_whenCategoryNotFound_shouldReturn404() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        when(createPostCommands.getObject()).thenReturn(createPostCommand);
        when(createPostCommand.execute(any(CreatePostCommandParams.class)))
                .thenThrow(CategoryNotFoundException.class);

        var body =
                new CreatePostRequest(
                        "Test post title",
                        "Test post content",
                        "MOCKED_AVATAR",
                        Set.of(UUID.randomUUID()),
                        "Test description",
                        PostLanguage.ENGLISH);

        mockMvc.perform(
                        post(Routes.build(Routes.Post.BASE))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPost_whenSlugConflict_shouldReturn409() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        when(createPostCommands.getObject()).thenReturn(createPostCommand);
        when(createPostCommand.execute(any(CreatePostCommandParams.class)))
                .thenThrow(new PostSlugAlreadyExistsException());

        var body =
                new CreatePostRequest(
                        "Test post title",
                        "Test post content",
                        "MOCKED_AVATAR",
                        Set.of(UUID.randomUUID()),
                        "Test description",
                        PostLanguage.ENGLISH);

        mockMvc.perform(
                        post(Routes.build(Routes.Post.BASE))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict());
    }

    @Test
    void updatePostBySlug_whenAuthenticatedAndValidRequest_shouldReturn200() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());
        var postDetail = mock(PostDetail.class);

        when(updatePostCommands.getObject()).thenReturn(updatePostCommand);
        when(updatePostCommand.execute(any(UpdatePostCommandParams.class))).thenReturn(postDetail);

        var body =
                new UpdatePostRequest(
                        "Updated title",
                        "Updated content",
                        "UPDATED_AVATAR",
                        Set.of(UUID.randomUUID()),
                        "Updated description",
                        "updated-title");

        mockMvc.perform(
                        put(Routes.build(Routes.Post.BASE, "existing-slug"))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    @Test
    void updatePostBySlug_whenNotAuthenticated_shouldReturn401() throws Exception {
        var body =
                new UpdatePostRequest(
                        "Updated title",
                        "Updated content",
                        "UPDATED_AVATAR",
                        Set.of(UUID.randomUUID()),
                        "Updated description",
                        "updated-title");

        mockMvc.perform(
                        put(Routes.build(Routes.Post.BASE, "existing-slug"))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updatePostBySlug_whenInvalidRequest_shouldReturn400() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        var body = new UpdatePostRequest(null, null, null, null, null, null);

        mockMvc.perform(
                        put(Routes.build(Routes.Post.BASE, "existing-slug"))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePostBySlug_whenPostNotFound_shouldReturn404() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        when(updatePostCommands.getObject()).thenReturn(updatePostCommand);
        when(updatePostCommand.execute(any(UpdatePostCommandParams.class)))
                .thenThrow(new PostNotFoundException());

        var body =
                new UpdatePostRequest(
                        "Updated title",
                        "Updated content",
                        "UPDATED_AVATAR",
                        Set.of(UUID.randomUUID()),
                        "Updated description",
                        "updated-title");

        mockMvc.perform(
                        put(Routes.build(Routes.Post.BASE, "missing-slug"))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePostBySlug_whenCategoryNotFound_shouldReturn404() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        when(updatePostCommands.getObject()).thenReturn(updatePostCommand);
        when(updatePostCommand.execute(any(UpdatePostCommandParams.class)))
                .thenThrow(new CategoryNotFoundException());

        var body =
                new UpdatePostRequest(
                        "Updated title",
                        "Updated content",
                        "UPDATED_AVATAR",
                        Set.of(UUID.randomUUID()),
                        "Updated description",
                        "updated-title");

        mockMvc.perform(
                        put(Routes.build(Routes.Post.BASE, "existing-slug"))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePostBySlug_whenSlugConflict_shouldReturn409() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        when(updatePostCommands.getObject()).thenReturn(updatePostCommand);
        when(updatePostCommand.execute(any(UpdatePostCommandParams.class)))
                .thenThrow(new PostSlugAlreadyExistsException());

        var body =
                new UpdatePostRequest(
                        "Updated title",
                        "Updated content",
                        "UPDATED_AVATAR",
                        Set.of(UUID.randomUUID()),
                        "Updated description",
                        "updated-title");

        mockMvc.perform(
                        put(Routes.build(Routes.Post.BASE, "existing-slug"))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict());
    }

    @Test
    void updatePostBySlug_whenNotOwner_shouldReturn403() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        when(updatePostCommands.getObject()).thenReturn(updatePostCommand);
        when(updatePostCommand.execute(any(UpdatePostCommandParams.class)))
                .thenThrow(new AccessDeniedException());

        var body =
                new UpdatePostRequest(
                        "Updated title",
                        "Updated content",
                        "UPDATED_AVATAR",
                        Set.of(UUID.randomUUID()),
                        "Updated description",
                        "updated-title");

        mockMvc.perform(
                        put(Routes.build(Routes.Post.BASE, "existing-slug"))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }
}

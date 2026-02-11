package com.github.mohrezal.api.domains.posts.controllers;

import static com.github.mohrezal.api.support.builders.CategoryBuilder.aCategory;
import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import com.github.mohrezal.api.domains.posts.dtos.SlugAvailability;
import com.github.mohrezal.api.domains.posts.dtos.UpdatePostRequest;
import com.github.mohrezal.api.domains.posts.enums.PostLanguage;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostInvalidStatusTransitionException;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostNotFoundException;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostSlugAlreadyExistsException;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostSlugFormatException;
import com.github.mohrezal.api.domains.posts.queries.GetPostBySlugQuery;
import com.github.mohrezal.api.domains.posts.queries.GetPostSlugAvailabilityQuery;
import com.github.mohrezal.api.domains.posts.queries.GetPostsBySearchQuery;
import com.github.mohrezal.api.domains.posts.queries.GetPostsQuery;
import com.github.mohrezal.api.domains.posts.queries.params.GetPostBySlugQueryParams;
import com.github.mohrezal.api.domains.posts.queries.params.GetPostSlugAvailabilityQueryParams;
import com.github.mohrezal.api.domains.posts.queries.params.GetPostsQueryParams;
import com.github.mohrezal.api.domains.users.enums.UserRole;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.dtos.PageResponse;
import com.github.mohrezal.api.shared.exceptions.SharedExceptionHandler;
import com.github.mohrezal.api.shared.exceptions.types.AccessDeniedException;
import com.github.mohrezal.api.shared.services.ratelimit.RateLimitService;
import com.github.mohrezal.api.support.builders.UserBuilder;
import com.github.mohrezal.api.support.security.AuthenticationUtils;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
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
    @MockitoBean private ObjectProvider<PublishPostCommand> publishPostCommands;

    @MockitoBean private ArchivePostCommand archivePostCommand;
    @MockitoBean private ObjectProvider<ArchivePostCommand> archivePostCommands;

    @MockitoBean private UnarchivePostCommand unarchivePostCommand;
    @MockitoBean private ObjectProvider<UnarchivePostCommand> unarchivePostCommands;

    @MockitoBean private DeletePostCommand deletePostCommand;
    @MockitoBean private ObjectProvider<DeletePostCommand> deletePostCommands;

    @MockitoBean private GetPostsQuery getPostsQuery;

    @MockitoBean private GetPostBySlugQuery getPostBySlugQuery;

    @MockitoBean private ObjectProvider<GetPostBySlugQuery> getPostBySlugQueries;

    @MockitoBean private GetPostSlugAvailabilityQuery getPostSlugAvailabilityQuery;

    @MockitoBean
    private ObjectProvider<GetPostSlugAvailabilityQuery> getPostSlugAvailabilityQueries;

    @MockitoBean private GetPostsBySearchQuery getPostsBySearchQuery;

    @Autowired private UserRepository userRepository;

    @Autowired private CategoryRepository categoryRepository;

    @MockitoBean private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        when(rateLimitService.tryConsume(anyString(), any()))
                .thenReturn(new RateLimitService.ConsumptionResult(true, 100L, 0L));
    }

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
    void getPosts_whenSizeExceedsMax_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(Routes.build(Routes.Post.BASE)).param("size", "21"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.size").value("Value cannot exceed 20"));
    }

    @Test
    void getPostBySlug_whenAuthenticatedAndPostExists_shouldReturn200() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());
        var postDetail = mock(PostDetail.class);

        when(getPostBySlugQueries.getObject()).thenReturn(getPostBySlugQuery);
        when(getPostBySlugQuery.execute(any(GetPostBySlugQueryParams.class)))
                .thenReturn(postDetail);

        mockMvc.perform(
                        get(Routes.build(Routes.Post.BASE, "existing-slug"))
                                .with(AuthenticationUtils.authenticate(user)))
                .andExpect(status().isOk());
    }

    @Test
    void getPostBySlug_whenNotAuthenticated_shouldReturn200() throws Exception {
        var postDetail = mock(PostDetail.class);

        when(getPostBySlugQueries.getObject()).thenReturn(getPostBySlugQuery);
        when(getPostBySlugQuery.execute(any(GetPostBySlugQueryParams.class)))
                .thenReturn(postDetail);

        mockMvc.perform(get(Routes.build(Routes.Post.BASE, "existing-slug")))
                .andExpect(status().isOk());
    }

    @Test
    void getPostBySlug_whenPostNotFound_shouldReturn404() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        when(getPostBySlugQueries.getObject()).thenReturn(getPostBySlugQuery);
        when(getPostBySlugQuery.execute(any(GetPostBySlugQueryParams.class)))
                .thenThrow(new PostNotFoundException());

        mockMvc.perform(
                        get(Routes.build(Routes.Post.BASE, "missing-slug"))
                                .with(AuthenticationUtils.authenticate(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getSlugAvailability_whenSlugAvailable_shouldReturn200() throws Exception {
        when(getPostSlugAvailabilityQueries.getObject()).thenReturn(getPostSlugAvailabilityQuery);
        when(getPostSlugAvailabilityQuery.execute(any(GetPostSlugAvailabilityQueryParams.class)))
                .thenReturn(new SlugAvailability(true, null));

        mockMvc.perform(
                        get(Routes.build(Routes.Post.BASE, Routes.Post.SLUG_AVAILABILITY))
                                .param("slug", "unique-slug"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.suggestion").value(nullValue()));
    }

    @Test
    void getSlugAvailability_whenSlugTaken_shouldReturn200WithSuggestion() throws Exception {
        when(getPostSlugAvailabilityQueries.getObject()).thenReturn(getPostSlugAvailabilityQuery);
        when(getPostSlugAvailabilityQuery.execute(any(GetPostSlugAvailabilityQueryParams.class)))
                .thenReturn(new SlugAvailability(false, "unique-slug-1"));

        mockMvc.perform(
                        get(Routes.build(Routes.Post.BASE, Routes.Post.SLUG_AVAILABILITY))
                                .param("slug", "taken-slug"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.suggestion").value("unique-slug-1"));
    }

    @Test
    void getSlugAvailability_whenInvalidSlug_shouldReturn400() throws Exception {
        when(getPostSlugAvailabilityQueries.getObject()).thenReturn(getPostSlugAvailabilityQuery);
        when(getPostSlugAvailabilityQuery.execute(any(GetPostSlugAvailabilityQueryParams.class)))
                .thenThrow(new PostSlugFormatException());

        mockMvc.perform(
                        get(Routes.build(Routes.Post.BASE, Routes.Post.SLUG_AVAILABILITY))
                                .param("slug", "Invalid Slug"))
                .andExpect(status().isBadRequest());
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

    @Test
    void publishPost_whenAuthenticatedAndValid_shouldReturn204() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        when(publishPostCommands.getObject()).thenReturn(publishPostCommand);
        doNothing()
                .when(publishPostCommand)
                .execute(
                        any(
                                com.github.mohrezal.api.domains.posts.commands.params
                                        .PublishPostCommandParams.class));

        mockMvc.perform(
                        post(Routes.build(Routes.Post.BASE, "existing-slug", "publish"))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user)))
                .andExpect(status().isNoContent());
    }

    @Test
    void publishPost_whenNotAuthenticated_shouldReturn401() throws Exception {
        mockMvc.perform(
                        post(Routes.build(Routes.Post.BASE, "existing-slug", "publish"))
                                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void publishPost_whenPostNotFound_shouldReturn404() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        when(publishPostCommands.getObject()).thenReturn(publishPostCommand);
        doThrow(new PostNotFoundException())
                .when(publishPostCommand)
                .execute(
                        any(
                                com.github.mohrezal.api.domains.posts.commands.params
                                        .PublishPostCommandParams.class));

        mockMvc.perform(
                        post(Routes.build(Routes.Post.BASE, "missing-slug", "publish"))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    void publishPost_whenInvalidStatus_shouldReturn400() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        when(publishPostCommands.getObject()).thenReturn(publishPostCommand);
        doThrow(new PostInvalidStatusTransitionException())
                .when(publishPostCommand)
                .execute(
                        any(
                                com.github.mohrezal.api.domains.posts.commands.params
                                        .PublishPostCommandParams.class));

        mockMvc.perform(
                        post(Routes.build(Routes.Post.BASE, "existing-slug", "publish"))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void publishPost_whenNotOwnerOrAdmin_shouldReturn403() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        when(publishPostCommands.getObject()).thenReturn(publishPostCommand);
        doThrow(new AccessDeniedException())
                .when(publishPostCommand)
                .execute(
                        any(
                                com.github.mohrezal.api.domains.posts.commands.params
                                        .PublishPostCommandParams.class));

        mockMvc.perform(
                        post(Routes.build(Routes.Post.BASE, "existing-slug", "publish"))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    void archivePost_whenAuthenticatedAndValid_shouldReturn204() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        when(archivePostCommands.getObject()).thenReturn(archivePostCommand);
        doNothing()
                .when(archivePostCommand)
                .execute(
                        any(
                                com.github.mohrezal.api.domains.posts.commands.params
                                        .ArchivePostCommandParams.class));

        mockMvc.perform(
                        post(Routes.build(Routes.Post.BASE, "existing-slug", "archive"))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user)))
                .andExpect(status().isNoContent());
    }

    @Test
    void archivePost_whenNotAuthenticated_shouldReturn401() throws Exception {
        mockMvc.perform(
                        post(Routes.build(Routes.Post.BASE, "existing-slug", "archive"))
                                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void archivePost_whenPostNotFound_shouldReturn404() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        when(archivePostCommands.getObject()).thenReturn(archivePostCommand);
        doThrow(new PostNotFoundException())
                .when(archivePostCommand)
                .execute(
                        any(
                                com.github.mohrezal.api.domains.posts.commands.params
                                        .ArchivePostCommandParams.class));

        mockMvc.perform(
                        post(Routes.build(Routes.Post.BASE, "missing-slug", "archive"))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    void archivePost_whenInvalidStatus_shouldReturn400() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        when(archivePostCommands.getObject()).thenReturn(archivePostCommand);
        doThrow(new PostInvalidStatusTransitionException())
                .when(archivePostCommand)
                .execute(
                        any(
                                com.github.mohrezal.api.domains.posts.commands.params
                                        .ArchivePostCommandParams.class));

        mockMvc.perform(
                        post(Routes.build(Routes.Post.BASE, "existing-slug", "archive"))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void archivePost_whenNotOwnerOrAdmin_shouldReturn403() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        when(archivePostCommands.getObject()).thenReturn(archivePostCommand);
        doThrow(new AccessDeniedException())
                .when(archivePostCommand)
                .execute(
                        any(
                                com.github.mohrezal.api.domains.posts.commands.params
                                        .ArchivePostCommandParams.class));

        mockMvc.perform(
                        post(Routes.build(Routes.Post.BASE, "existing-slug", "archive"))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    void unarchivePost_whenAuthenticatedAndValid_shouldReturn204() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        when(unarchivePostCommands.getObject()).thenReturn(unarchivePostCommand);
        doNothing()
                .when(unarchivePostCommand)
                .execute(
                        any(
                                com.github.mohrezal.api.domains.posts.commands.params
                                        .UnarchivePostCommandParams.class));

        mockMvc.perform(
                        post(Routes.build(Routes.Post.BASE, "existing-slug", "unarchive"))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user)))
                .andExpect(status().isNoContent());
    }

    @Test
    void unarchivePost_whenNotAuthenticated_shouldReturn401() throws Exception {
        mockMvc.perform(
                        post(Routes.build(Routes.Post.BASE, "existing-slug", "unarchive"))
                                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unarchivePost_whenPostNotFound_shouldReturn404() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        when(unarchivePostCommands.getObject()).thenReturn(unarchivePostCommand);
        doThrow(new PostNotFoundException())
                .when(unarchivePostCommand)
                .execute(
                        any(
                                com.github.mohrezal.api.domains.posts.commands.params
                                        .UnarchivePostCommandParams.class));

        mockMvc.perform(
                        post(Routes.build(Routes.Post.BASE, "missing-slug", "unarchive"))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    void unarchivePost_whenInvalidStatus_shouldReturn400() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        when(unarchivePostCommands.getObject()).thenReturn(unarchivePostCommand);
        doThrow(new PostInvalidStatusTransitionException())
                .when(unarchivePostCommand)
                .execute(
                        any(
                                com.github.mohrezal.api.domains.posts.commands.params
                                        .UnarchivePostCommandParams.class));

        mockMvc.perform(
                        post(Routes.build(Routes.Post.BASE, "existing-slug", "unarchive"))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unarchivePost_whenNotOwnerOrAdmin_shouldReturn403() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        when(unarchivePostCommands.getObject()).thenReturn(unarchivePostCommand);
        doThrow(new AccessDeniedException())
                .when(unarchivePostCommand)
                .execute(
                        any(
                                com.github.mohrezal.api.domains.posts.commands.params
                                        .UnarchivePostCommandParams.class));

        mockMvc.perform(
                        post(Routes.build(Routes.Post.BASE, "existing-slug", "unarchive"))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletePostBySlug_whenAuthenticatedAndValid_shouldReturn204() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        when(deletePostCommands.getObject()).thenReturn(deletePostCommand);
        doNothing()
                .when(deletePostCommand)
                .execute(
                        any(
                                com.github.mohrezal.api.domains.posts.commands.params
                                        .DeletePostCommandParams.class));

        mockMvc.perform(
                        delete(Routes.build(Routes.Post.BASE, "existing-slug"))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePostBySlug_whenNotAuthenticated_shouldReturn401() throws Exception {
        mockMvc.perform(delete(Routes.build(Routes.Post.BASE, "existing-slug")).with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deletePostBySlug_whenPostNotFound_shouldReturn404() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        when(deletePostCommands.getObject()).thenReturn(deletePostCommand);
        doThrow(new PostNotFoundException())
                .when(deletePostCommand)
                .execute(
                        any(
                                com.github.mohrezal.api.domains.posts.commands.params
                                        .DeletePostCommandParams.class));

        mockMvc.perform(
                        delete(Routes.build(Routes.Post.BASE, "missing-slug"))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePostBySlug_whenNotOwner_shouldReturn403() throws Exception {
        var user =
                userRepository.save(
                        aUser().withEmail("user@test.com").withRole(UserRole.USER).build());

        when(deletePostCommands.getObject()).thenReturn(deletePostCommand);
        doThrow(new AccessDeniedException())
                .when(deletePostCommand)
                .execute(
                        any(
                                com.github.mohrezal.api.domains.posts.commands.params
                                        .DeletePostCommandParams.class));

        mockMvc.perform(
                        delete(Routes.build(Routes.Post.BASE, "existing-slug"))
                                .with(csrf())
                                .with(AuthenticationUtils.authenticate(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPostsBySearchQuery_whenNoResults_shouldReturnEmptyPage() throws Exception {
        Page<PostSummary> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        when(getPostsBySearchQuery.execute(any()))
                .thenReturn(PageResponse.from(emptyPage, post -> post));

        mockMvc.perform(
                        get(Routes.build(Routes.Post.BASE, Routes.Post.SEARCH))
                                .param("query", "nothing")
                                .param("page", "0")
                                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.isEmpty").value(true));
    }

    @Test
    void getPostsBySearchQuery_whenResultsExist_shouldReturnPage() throws Exception {
        var summary = mock(PostSummary.class);
        Page<PostSummary> page = new PageImpl<>(List.of(summary), PageRequest.of(0, 10), 1);

        when(getPostsBySearchQuery.execute(any()))
                .thenReturn(PageResponse.from(page, post -> post));

        mockMvc.perform(
                        get(Routes.build(Routes.Post.BASE, Routes.Post.SEARCH))
                                .param("query", "spring boot")
                                .param("page", "0")
                                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.isEmpty").value(false));
    }
}

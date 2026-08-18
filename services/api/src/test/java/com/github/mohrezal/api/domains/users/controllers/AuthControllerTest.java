package com.github.mohrezal.api.domains.users.controllers;

import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.mohrezal.api.config.Routes;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.services.ratelimit.RateLimitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private UserRepository userRepository;

    @MockitoBean private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        when(rateLimitService.tryConsume(anyString(), any()))
                .thenReturn(new RateLimitService.ConsumptionResult(true, 100L, 0L));
    }

    @Test
    void getEmailAvailability_whenAnonymousAndAvailable_shouldReturn200() throws Exception {
        mockMvc.perform(
                        get(Routes.build(Routes.Auth.BASE, Routes.Auth.EMAIL_AVAILABILITY))
                                .param("email", "user@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getEmailAvailability_whenInvalid_shouldReturn400() throws Exception {
        mockMvc.perform(
                        get(Routes.build(Routes.Auth.BASE, Routes.Auth.EMAIL_AVAILABILITY))
                                .param("email", "not-an-email"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Must be a valid email address"));
    }

    @Test
    void getEmailAvailability_whenAliasIsTaken_shouldReturnUnavailable() throws Exception {
        userRepository.save(
                aUser().withEmail("user@gmail.com").withHandle("existing_user").build());

        mockMvc.perform(
                        get(Routes.build(Routes.Auth.BASE, Routes.Auth.EMAIL_AVAILABILITY))
                                .param("email", "User+news@Gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void getHandleAvailability_whenAnonymousAndAvailable_shouldReturn200() throws Exception {
        mockMvc.perform(
                        get(Routes.build(Routes.Auth.BASE, Routes.Auth.HANDLE_AVAILABILITY))
                                .param("handle", "john_doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getHandleAvailability_whenInvalid_shouldReturn400() throws Exception {
        mockMvc.perform(
                        get(Routes.build(Routes.Auth.BASE, Routes.Auth.HANDLE_AVAILABILITY))
                                .param("handle", "ab"))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message")
                                .value("Only lowercase letters, numbers, and underscores"));
    }

    @Test
    void getHandleAvailability_whenReserved_shouldReturnUnavailable() throws Exception {
        mockMvc.perform(
                        get(Routes.build(Routes.Auth.BASE, Routes.Auth.HANDLE_AVAILABILITY))
                                .param("handle", "Admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void getHandleAvailability_whenTaken_shouldReturnUnavailable() throws Exception {
        userRepository.save(aUser().withEmail("taken@example.com").withHandle("john_doe").build());

        mockMvc.perform(
                        get(Routes.build(Routes.Auth.BASE, Routes.Auth.HANDLE_AVAILABILITY))
                                .param("handle", "John_Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }
}

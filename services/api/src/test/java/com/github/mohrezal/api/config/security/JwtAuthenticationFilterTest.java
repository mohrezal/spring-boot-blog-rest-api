package com.github.mohrezal.api.config.security;

import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.common.constants.CookieConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock private JwtTokenProvider jwtTokenProvider;

    @Mock private UserRepository userRepository;

    @Mock private FilterChain filterChain;

    @InjectMocks private JwtAuthenticationFilter filter;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_whenUserIsNotVerified_shouldNotAuthenticate() throws Exception {
        var userId = UUID.randomUUID();
        var user = aUser().withId(userId).withIsVerified(false).build();
        var request = requestWithAccessToken("access-token");
        var response = new MockHttpServletResponse();

        when(jwtTokenProvider.isAccessToken("access-token")).thenReturn(true);
        when(jwtTokenProvider.extractUserId("access-token")).thenReturn(Optional.of(userId));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_whenUserIsVerified_shouldAuthenticate() throws Exception {
        var userId = UUID.randomUUID();
        var user = aUser().withId(userId).withIsVerified(true).build();
        var request = requestWithAccessToken("access-token");
        var response = new MockHttpServletResponse();

        when(jwtTokenProvider.isAccessToken("access-token")).thenReturn(true);
        when(jwtTokenProvider.extractUserId("access-token")).thenReturn(Optional.of(userId));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtTokenProvider.extractPrivilegeVersion("access-token")).thenReturn(0L);
        when(jwtTokenProvider.extractPermissionKeys("access-token")).thenReturn(List.of());

        filter.doFilter(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertTrue(authentication.isAuthenticated());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_whenTokenIsRefreshType_shouldNotAuthenticate() throws Exception {
        var request = requestWithAccessToken("refresh-token");
        var response = new MockHttpServletResponse();

        when(jwtTokenProvider.isAccessToken("refresh-token")).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(jwtTokenProvider).isAccessToken("refresh-token");
    }

    private static MockHttpServletRequest requestWithAccessToken(String token) {
        var request = new MockHttpServletRequest();
        request.setCookies(new Cookie(CookieConstants.ACCESS_TOKEN_COOKIE_NAME, token));
        return request;
    }
}

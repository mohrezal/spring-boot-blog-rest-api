package com.github.mohrezal.api.config.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.shared.config.ApplicationProperties;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    private static final String SECRET =
            "dGVzdHNlY3JldGtleWZvcmp3dHNpZ25pbmd0aGF0aXNsb25nZW5vdWdoZm9yaHM1MTJhbGdvcml0aG1yZXF1aXJlbWVudHM=";

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        ApplicationProperties applicationProperties = mock(ApplicationProperties.class);
        ApplicationProperties.Security security = mock(ApplicationProperties.Security.class);
        when(applicationProperties.security()).thenReturn(security);
        when(security.secret()).thenReturn(SECRET);
        when(security.accessTokenLifeTime()).thenReturn(Duration.ofHours(1));
        when(security.refreshTokenLifeTime()).thenReturn(Duration.ofDays(7));
        jwtTokenProvider = new JwtTokenProvider(applicationProperties);
    }

    @Test
    void createAccessToken_shouldEmbedUserIdPermissionsAndPrivilegeVersion() {
        var userId = UUID.randomUUID();
        var permissions = List.of("post:create", "user:read");
        var privilegeVersion = 3L;

        var token = jwtTokenProvider.createAccessToken(userId, permissions, privilegeVersion);

        assertEquals(Optional.of(userId), jwtTokenProvider.extractUserId(token));
        assertEquals(permissions, jwtTokenProvider.extractPermissionKeys(token));
        assertEquals(privilegeVersion, jwtTokenProvider.extractPrivilegeVersion(token));
        assertEquals(Optional.of(JwtClaim.TYPE_ACCESS), jwtTokenProvider.extractTokenType(token));
        assertTrue(jwtTokenProvider.isAccessToken(token));
        assertFalse(jwtTokenProvider.isRefreshToken(token));
    }

    @Test
    void createAccessToken_shouldSetExpirationWithinAccessTokenLifetime() {
        var now = OffsetDateTime.now(ZoneOffset.UTC);

        var token = jwtTokenProvider.createAccessToken(UUID.randomUUID(), List.of(), 0L);

        var expiration = jwtTokenProvider.extractExpiration(token);
        assertTrue(expiration.isPresent());
        assertTrue(expiration.get().isAfter(now));
        assertTrue(expiration.get().isBefore(now.plusHours(1).plusMinutes(1)));
    }

    @Test
    void createRefreshToken_shouldEmbedUserIdWithoutPermissions() {
        var userId = UUID.randomUUID();

        var token = jwtTokenProvider.createRefreshToken(userId);

        assertEquals(Optional.of(userId), jwtTokenProvider.extractUserId(token));
        assertEquals(List.of(), jwtTokenProvider.extractPermissionKeys(token));
        assertEquals(0L, jwtTokenProvider.extractPrivilegeVersion(token));
        assertEquals(Optional.of(JwtClaim.TYPE_REFRESH), jwtTokenProvider.extractTokenType(token));
        assertTrue(jwtTokenProvider.isRefreshToken(token));
        assertFalse(jwtTokenProvider.isAccessToken(token));
    }

    @Test
    void extractTokenType_whenTokenHasNoType_shouldReturnEmpty() {
        assertEquals(Optional.empty(), jwtTokenProvider.extractTokenType("not-a-jwt"));
        assertFalse(jwtTokenProvider.isAccessToken("not-a-jwt"));
        assertFalse(jwtTokenProvider.isRefreshToken("not-a-jwt"));
    }

    @Test
    void extractUserId_whenTokenIsInvalid_shouldReturnEmpty() {
        assertEquals(Optional.empty(), jwtTokenProvider.extractUserId("not-a-jwt"));
    }

    @Test
    void extractExpiration_whenTokenIsInvalid_shouldReturnEmpty() {
        assertFalse(jwtTokenProvider.extractExpiration("not-a-jwt").isPresent());
    }
}

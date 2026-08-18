package com.github.mohrezal.api.shared.services.jwt;

import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.config.security.JwtTokenProvider;
import com.github.mohrezal.api.domains.privilege.constant.Permissions;
import com.github.mohrezal.api.domains.privilege.service.UserPermissionService;
import com.github.mohrezal.api.domains.users.repositories.RefreshTokenRepository;
import com.github.mohrezal.api.shared.config.ApplicationProperties;
import com.github.mohrezal.api.shared.services.hash.HashService;
import com.github.mohrezal.common.redis.RedisCacheService;
import com.github.mohrezal.common.redis.constants.RedisKeyFactory;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    private static final String SECRET =
            "dGVzdHNlY3JldGtleWZvcmp3dHNpZ25pbmd0aGF0aXNsb25nZW5vdWdoZm9yaHM1MTJhbGdvcml0aG1yZXF1aXJlbWVudHM=";

    private JwtTokenProvider jwtTokenProvider;

    @Mock private UserPermissionService userPermissionService;

    @Mock private RefreshTokenRepository refreshTokenRepository;

    @Mock private HashService hashService;

    @Mock private RedisCacheService redisCacheService;

    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        ApplicationProperties applicationProperties = mock(ApplicationProperties.class);
        ApplicationProperties.Security security = mock(ApplicationProperties.Security.class);
        when(applicationProperties.security()).thenReturn(security);
        when(security.secret()).thenReturn(SECRET);
        lenient().when(security.accessTokenLifeTime()).thenReturn(Duration.ofHours(1));
        lenient().when(security.refreshTokenLifeTime()).thenReturn(Duration.ofDays(7));

        jwtTokenProvider = new JwtTokenProvider(applicationProperties);
        jwtService =
                new JwtServiceImpl(
                        jwtTokenProvider,
                        refreshTokenRepository,
                        hashService,
                        userPermissionService,
                        redisCacheService);
    }

    @Test
    void generateAccessToken_shouldEmbedPermissionKeysAndPrivilegeVersion() {
        var userId = UUID.randomUUID();
        var privilegeVersion = 5L;
        var user = aUser().withId(userId).withPrivilegeVersion(privilegeVersion).build();
        var permissions = List.of(Permissions.BLOG_POSTS_CREATE, Permissions.BLOG_POSTS_UPDATE);

        when(userPermissionService.getPermissionKeys(userId)).thenReturn(permissions);

        var token = jwtService.generateAccessToken(user);

        assertEquals(Optional.of(userId), jwtTokenProvider.extractUserId(token));
        assertEquals(permissions, jwtTokenProvider.extractPermissionKeys(token));
        assertEquals(privilegeVersion, jwtTokenProvider.extractPrivilegeVersion(token));
        assertTrue(jwtTokenProvider.isAccessToken(token));
        verify(userPermissionService).getPermissionKeys(userId);
    }

    @Test
    void validateToken_whenTokenIsAccessType_shouldReturnFalse() {
        var userId = UUID.randomUUID();
        var user = aUser().withId(userId).build();
        when(userPermissionService.getPermissionKeys(userId)).thenReturn(List.of());

        var accessToken = jwtService.generateAccessToken(user);

        assertFalse(jwtService.validateToken(accessToken));
    }

    @Test
    void validateToken_whenTokenIsRefreshType_shouldReturnTrue() {
        var refreshToken = jwtService.generateRefreshToken(UUID.randomUUID());

        assertTrue(jwtService.validateToken(refreshToken));
    }

    @Test
    void revokeAccessToken_whenTokenIsValid_shouldStoreHashUntilExpiry() {
        var userId = UUID.randomUUID();
        var user = aUser().withId(userId).build();
        when(userPermissionService.getPermissionKeys(userId)).thenReturn(List.of());
        when(hashService.sha256(any())).thenReturn("access-hash");

        var accessToken = jwtService.generateAccessToken(user);
        jwtService.revokeAccessToken(accessToken);

        verify(redisCacheService)
                .set(
                        eq(RedisKeyFactory.AccessToken.revoked("access-hash")),
                        eq("1"),
                        any(Duration.class));
    }

    @Test
    void revokeAccessToken_whenTokenIsBlank_shouldSkip() {
        jwtService.revokeAccessToken("  ");

        verify(redisCacheService, never()).set(any(), any(), any());
    }

    @Test
    void isAccessTokenRevoked_whenHashIsPresent_shouldReturnTrue() {
        when(hashService.sha256("access-token")).thenReturn("access-hash");
        when(redisCacheService.get(
                        RedisKeyFactory.AccessToken.revoked("access-hash"), String.class))
                .thenReturn(Optional.of("1"));

        assertTrue(jwtService.isAccessTokenRevoked("access-token"));
    }

    @Test
    void isAccessTokenRevoked_whenHashIsMissing_shouldReturnFalse() {
        when(hashService.sha256("access-token")).thenReturn("access-hash");
        when(redisCacheService.get(
                        RedisKeyFactory.AccessToken.revoked("access-hash"), String.class))
                .thenReturn(Optional.empty());

        assertFalse(jwtService.isAccessTokenRevoked("access-token"));
    }
}

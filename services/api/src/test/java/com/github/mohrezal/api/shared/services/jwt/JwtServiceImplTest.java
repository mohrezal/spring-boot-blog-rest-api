package com.github.mohrezal.api.shared.services.jwt;

import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.config.security.JwtTokenProvider;
import com.github.mohrezal.api.domains.privilege.constant.Permissions;
import com.github.mohrezal.api.domains.privilege.service.UserPermissionService;
import com.github.mohrezal.api.domains.users.repositories.RefreshTokenRepository;
import com.github.mohrezal.api.shared.config.ApplicationProperties;
import com.github.mohrezal.api.shared.services.hash.HashService;
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

    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        ApplicationProperties applicationProperties = mock(ApplicationProperties.class);
        ApplicationProperties.Security security = mock(ApplicationProperties.Security.class);
        when(applicationProperties.security()).thenReturn(security);
        when(security.secret()).thenReturn(SECRET);
        when(security.accessTokenLifeTime()).thenReturn(Duration.ofHours(1));

        jwtTokenProvider = new JwtTokenProvider(applicationProperties);
        jwtService =
                new JwtServiceImpl(
                        jwtTokenProvider,
                        refreshTokenRepository,
                        hashService,
                        userPermissionService);
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
        verify(userPermissionService).getPermissionKeys(userId);
    }
}

package com.github.mohrezal.api.shared.services.jwt;

import com.github.mohrezal.api.domains.users.models.RefreshToken;
import com.github.mohrezal.api.domains.users.models.User;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface JwtService {
    String generateAccessToken(User user);

    String generateRefreshToken(UUID userId);

    void saveRefreshToken(
            String token, User user, String ipAddress, String userAgent, String deviceName);

    boolean validateToken(String token);

    boolean validateRefreshToken(String token);

    Optional<RefreshToken> getRefreshTokenEntity(String token);

    boolean revokeRefreshTokenIfActive(String token);

    void revokeRefreshToken(String token);

    void revokeAccessToken(String token);

    boolean isAccessTokenRevoked(String token);

    void revokeAllUserRefreshTokens(UUID userId);

    UUID getUserIdFromToken(String token);

    Instant getExpirationFromToken(String token);

    boolean isTokenExpired(String token);
}

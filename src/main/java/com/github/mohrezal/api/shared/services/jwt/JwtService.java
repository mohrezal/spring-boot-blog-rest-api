package com.github.mohrezal.api.shared.services.jwt;

import com.github.mohrezal.api.domains.users.models.RefreshToken;
import com.github.mohrezal.api.domains.users.models.User;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.oauth2.jwt.Jwt;

public interface JwtService {
    String generateAccessToken(User user);

    String generateRefreshToken(UUID userId);

    void saveRefreshToken(
            String token, User user, String ipAddress, String userAgent, String deviceName);

    Jwt decodeToken(String token);

    boolean validateToken(String token);

    boolean validateRefreshToken(String token);

    Optional<RefreshToken> getRefreshTokenEntity(String token);

    void revokeRefreshToken(String token);

    void revokeAllUserRefreshTokens(UUID userId);

    UUID getUserIdFromToken(String token);

    String getUsernameFromToken(String token);

    Instant getExpirationFromToken(String token);

    boolean isTokenExpired(String token);
}

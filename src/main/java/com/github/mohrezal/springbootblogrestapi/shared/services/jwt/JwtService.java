package com.github.mohrezal.springbootblogrestapi.shared.services.jwt;

import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import java.time.Instant;
import java.util.UUID;
import org.springframework.security.oauth2.jwt.Jwt;

public interface JwtService {
    String generateAccessToken(User user);

    String generateRefreshToken(UUID userId);

    Jwt decodeToken(String token);

    boolean validateToken(String token);

    UUID getUserIdFromToken(String token);

    String getUsernameFromToken(String token);

    Instant getExpirationFromToken(String token);

    boolean isTokenExpired(String token);
}

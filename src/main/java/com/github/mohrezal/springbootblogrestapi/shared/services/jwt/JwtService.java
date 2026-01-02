package com.github.mohrezal.springbootblogrestapi.shared.services.jwt;

import java.time.Instant;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

public interface JwtService {
    String generateAccessToken(UUID userId, Authentication authentication);

    String generateRefreshToken(UUID userId, Authentication authentication);

    String generateToken(UUID userId, Authentication authentication, Long expirationTimeInSeconds);

    Jwt decodeToken(String token);

    boolean validateToken(String token);

    UUID getUserIdFromToken(String token);

    String getUsernameFromToken(String token);

    Instant getExpirationFromToken(String token);

    boolean isTokenExpired(String token);
}

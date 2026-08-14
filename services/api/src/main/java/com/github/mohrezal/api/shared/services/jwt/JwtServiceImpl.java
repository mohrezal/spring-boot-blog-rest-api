package com.github.mohrezal.api.shared.services.jwt;

import com.github.mohrezal.api.config.security.JwtTokenProvider;
import com.github.mohrezal.api.domains.privilege.service.UserPermissionService;
import com.github.mohrezal.api.domains.users.models.RefreshToken;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.repositories.RefreshTokenRepository;
import com.github.mohrezal.api.shared.services.hash.HashService;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final HashService hashService;
    private final UserPermissionService userPermissionService;

    @Override
    public String generateAccessToken(User user) {
        var permissions = userPermissionService.getPermissionKeys(user.getId());
        return jwtTokenProvider.createAccessToken(
                user.getId(), permissions, user.getPrivilegeVersion());
    }

    @Override
    public String generateRefreshToken(UUID userId) {
        return jwtTokenProvider.createRefreshToken(userId);
    }

    public boolean validateToken(String token) {
        return jwtTokenProvider
                .extractExpiration(token)
                .map(expiration -> expiration.toInstant().isAfter(Instant.now()))
                .orElse(false);
    }

    public UUID getUserIdFromToken(String token) {
        return jwtTokenProvider
                .extractUserId(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
    }

    public Instant getExpirationFromToken(String token) {
        return jwtTokenProvider
                .extractExpiration(token)
                .map(OffsetDateTime::toInstant)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
    }

    public boolean isTokenExpired(String token) {
        try {
            Instant expiration = getExpirationFromToken(token);
            return expiration != null && expiration.isBefore(Instant.now());
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    @Transactional
    public void saveRefreshToken(
            String token, User user, String ipAddress, String userAgent, String deviceName) {
        String tokenHash = hashService.sha256(token);

        Instant expiration = getExpirationFromToken(token);
        OffsetDateTime expiresAt = OffsetDateTime.ofInstant(expiration, ZoneOffset.UTC);

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .user(user)
                        .tokenHash(tokenHash)
                        .expiresAt(expiresAt)
                        .ipAddress(ipAddress)
                        .userAgent(userAgent)
                        .deviceName(deviceName)
                        .build();

        refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateRefreshToken(String token) {
        try {
            if (!validateToken(token)) {
                return false;
            }

            String tokenHash = hashService.sha256(token);
            RefreshToken refreshToken =
                    refreshTokenRepository.findByTokenHash(tokenHash).orElse(null);

            return refreshToken != null && refreshToken.isValid();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> getRefreshTokenEntity(String token) {
        String tokenHash = hashService.sha256(token);
        return refreshTokenRepository.findByTokenHash(tokenHash);
    }

    @Override
    @Transactional
    public boolean revokeRefreshTokenIfActive(String token) {
        String tokenHash = hashService.sha256(token);
        OffsetDateTime revokedAt = OffsetDateTime.now();
        return refreshTokenRepository.revokeTokenIfActive(tokenHash, revokedAt) > 0;
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String token) {
        String tokenHash = hashService.sha256(token);
        refreshTokenRepository
                .findByTokenHash(tokenHash)
                .ifPresent(
                        refreshToken -> {
                            refreshToken.revoke();
                            refreshTokenRepository.save(refreshToken);
                        });
    }

    @Override
    @Transactional
    public void revokeAllUserRefreshTokens(UUID userId) {
        refreshTokenRepository.revokeAllUserTokens(userId, OffsetDateTime.now());
    }
}

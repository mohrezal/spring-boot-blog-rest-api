package com.github.mohrezal.api.shared.services.jwt;

import com.github.mohrezal.api.domains.users.models.RefreshToken;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.repositories.RefreshTokenRepository;
import com.github.mohrezal.api.shared.config.ApplicationProperties;
import com.github.mohrezal.api.shared.services.hash.HashService;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final ApplicationProperties applicationProperties;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final HashService hashService;

    @Override
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plus(applicationProperties.security().accessTokenLifeTime());

        List<String> roles =
                user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .issuer("self")
                        .issuedAt(now)
                        .expiresAt(expiration)
                        .subject(user.getId().toString())
                        .claim("username", user.getUsername())
                        .claim("scope", roles)
                        .id(UUID.randomUUID().toString())
                        .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public String generateRefreshToken(UUID userId) {
        Instant now = Instant.now();
        Instant expiration = now.plus(applicationProperties.security().refreshTokenLifeTime());

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .issuer("self")
                        .issuedAt(now)
                        .expiresAt(expiration)
                        .subject(userId.toString())
                        .id(UUID.randomUUID().toString())
                        .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public Jwt decodeToken(String token) {
        return jwtDecoder.decode(token);
    }

    public boolean validateToken(String token) {
        try {
            Jwt jwt = decodeToken(token);
            return jwt.getExpiresAt() != null && jwt.getExpiresAt().isAfter(Instant.now());
        } catch (Exception e) {
            return false;
        }
    }

    public UUID getUserIdFromToken(String token) {
        String subject = decodeToken(token).getSubject();
        return UUID.fromString(subject);
    }

    public String getUsernameFromToken(String token) {
        return decodeToken(token).getClaimAsString("username");
    }

    public List<String> getRolesFromToken(String token) {
        Jwt jwt = decodeToken(token);
        return jwt.getClaimAsStringList("scope");
    }

    public Instant getExpirationFromToken(String token) {
        return decodeToken(token).getExpiresAt();
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

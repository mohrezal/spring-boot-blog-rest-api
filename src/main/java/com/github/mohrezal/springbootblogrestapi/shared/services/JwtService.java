package com.github.mohrezal.springbootblogrestapi.shared.services;

import com.github.mohrezal.springbootblogrestapi.shared.config.ApplicationProperties;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final ApplicationProperties applicationProperties;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public JwtService(ApplicationProperties applicationProperties, JwtDecoder jwtDecoder) {
        this.applicationProperties = applicationProperties;
        this.jwtDecoder = jwtDecoder;
        byte[] keyBytes =
                java.util.Base64.getDecoder().decode(applicationProperties.security().secret());
        SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA512");
        this.jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }

    public String generateAccessToken(UUID userId, Authentication authentication) {
        return generateToken(
                userId, authentication, applicationProperties.security().accessTokenLifeTime());
    }

    public String generateRefreshToken(UUID userId, Authentication authentication) {
        return generateToken(
                userId, authentication, applicationProperties.security().refreshTokenLifeTime());
    }

    public String generateToken(
            UUID userId, Authentication authentication, Long expirationTimeInSeconds) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(expirationTimeInSeconds);

        List<String> roles =
                authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList();

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .issuer("self")
                        .issuedAt(now)
                        .expiresAt(expiration)
                        .subject(userId.toString())
                        .claim("username", authentication.getName())
                        .claim("scope", roles)
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
}

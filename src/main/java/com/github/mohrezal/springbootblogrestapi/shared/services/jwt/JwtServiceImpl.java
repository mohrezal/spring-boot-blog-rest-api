package com.github.mohrezal.springbootblogrestapi.shared.services.jwt;

import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.shared.config.ApplicationProperties;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final ApplicationProperties applicationProperties;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Override
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiration =
                now.plusSeconds(applicationProperties.security().accessTokenLifeTime());

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
                        .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public String generateRefreshToken(UUID userId) {
        Instant now = Instant.now();
        Instant expiration =
                now.plusSeconds(applicationProperties.security().refreshTokenLifeTime());

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .issuer("self")
                        .issuedAt(now)
                        .expiresAt(expiration)
                        .subject(userId.toString())
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

package com.github.mohrezal.api.domains.users.models;

import com.github.mohrezal.api.shared.models.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
            @Index(
                    name = "idx_refresh_tokens_token_hash",
                    columnList = "token_hash",
                    unique = true),
            @Index(name = "idx_refresh_tokens_user_id", columnList = "user_id"),
            @Index(name = "idx_refresh_tokens_expires_at", columnList = "expires_at"),
            @Index(
                    name = "idx_refresh_tokens_user_id_revoked_at",
                    columnList = "user_id, revoked_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RefreshToken extends BaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(name = "revoked_at")
    private OffsetDateTime revokedAt;

    @Column(name = "device_name", length = 255)
    private String deviceName;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    public boolean isValid() {
        return revokedAt == null && expiresAt.isAfter(OffsetDateTime.now());
    }

    public boolean isExpired() {
        return expiresAt.isBefore(OffsetDateTime.now());
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public void revoke() {
        this.revokedAt = OffsetDateTime.now();
    }
}

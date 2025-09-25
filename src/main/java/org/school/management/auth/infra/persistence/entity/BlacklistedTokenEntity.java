package org.school.management.auth.infra.persistence.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "blacklisted_tokens", indexes = {
        @Index(name = "idx_token_hash", columnList = "token_hash", unique = true),
        @Index(name = "idx_expires_at", columnList = "expires_at"),
        @Index(name = "idx_user_email", columnList = "user_email"),
        @Index(name = "idx_token_type", columnList = "token_type"),
        @Index(name = "idx_blacklisted_at", columnList = "blacklisted_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BlacklistedTokenEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash; // SHA-256 hash del token

    @Column(name = "token_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    @Column(name = "blacklisted_at", nullable = false)
    private LocalDateTime blacklistedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "reason", length = 100)
    private String reason;

    @Column(name = "user_email", nullable = false, length = 254)
    private String userEmail;

    @PrePersist
    protected void onCreate() {
        if (blacklistedAt == null) {
            blacklistedAt = LocalDateTime.now();
        }
    }

    public enum TokenType {
        ACCESS, REFRESH, CONFIRMATION
    }

    // Helper methods
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isActive() {
        return !isExpired();
    }
}
package org.school.management.auth.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.school.management.shared.person.domain.valueobject.Dni;

import java.time.LocalDateTime;

@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RefreshToken {

    @EqualsAndHashCode.Include
    private final RefreshTokenId id;

    private final Dni userDni;
    private final String tokenHash;

    private final LocalDateTime issuedAt;
    private final LocalDateTime expiresAt;

    private LocalDateTime revokedAt;
    private String replacedByTokenHash;

    private final String deviceInfo;
    private final String ipAddress;
    private final String userAgent;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public void revoke(String replacedByTokenHash) {
        this.revokedAt = LocalDateTime.now();
        this.replacedByTokenHash = replacedByTokenHash;
    }
}
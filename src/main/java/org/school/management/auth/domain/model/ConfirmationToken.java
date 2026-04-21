package org.school.management.auth.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.school.management.auth.domain.valueobject.ConfirmationTokenId;
import org.school.management.shared.person.domain.valueobject.Dni;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ConfirmationToken {

    @EqualsAndHashCode.Include
    private final ConfirmationTokenId id;

    private final Dni userDni;
    private final String tokenHash;

    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;
    private LocalDateTime usedAt;

    public static ConfirmationToken create(Dni dni, String tokenHash, Duration ttl) {
        LocalDateTime now = LocalDateTime.now();

        return ConfirmationToken.builder()
                .id(ConfirmationTokenId.generate())
                .userDni(dni)
                .tokenHash(tokenHash)
                .createdAt(now)
                .expiresAt(now.plus(ttl))
                .build();
    }

    public void markAsUsed() {
        if (this.usedAt != null) {
            throw new IllegalStateException("Confirmation token already used");
        }
        this.usedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isUsed() {
        return usedAt != null;
    }
}

package org.school.management.auth.domain.model;

import lombok.*;
import org.school.management.auth.domain.valueobject.BlacklistedTokenId;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@ToString(exclude = "token") // No mostrar token en logs por seguridad
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistedToken {

    @EqualsAndHashCode.Include
    private BlacklistedTokenId id;

    private String tokenHash; // Hash del token, no el token completo por seguridad

    private String tokenType; // ACCESS, REFRESH, CONFIRMATION

    private LocalDateTime blacklistedAt;

    private LocalDateTime expiresAt;

    private String reason; // Razón del blacklist: LOGOUT, PASSWORD_CHANGED, ACCOUNT_DISABLED, etc.

    private String userDni; // Para auditoría

    // ============================================
    // Factory Methods
    // ============================================

    public static BlacklistedToken create(String tokenHash, String tokenType,
                                          LocalDateTime expiresAt, String reason, String userEmail) {
        return new BlacklistedToken(
                BlacklistedTokenId.generate(),
                tokenHash,
                tokenType,
                LocalDateTime.now(),
                expiresAt,
                reason,
                userEmail
        );
    }

    public static BlacklistedToken reconstruct(BlacklistedTokenId id, String tokenHash,
                                               String tokenType, LocalDateTime blacklistedAt,
                                               LocalDateTime expiresAt, String reason, String userEmail) {
        return new BlacklistedToken(id, tokenHash, tokenType, blacklistedAt, expiresAt, reason, userEmail);
    }

    // ============================================
    // Domain Methods
    // ============================================

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isActive() {
        return !isExpired();
    }

    // Enum para razones de blacklist
    public enum BlacklistReason {
        LOGOUT("User logged out"),
        PASSWORD_CHANGED("Password was changed"),
        ACCOUNT_DISABLED("Account was disabled"),
        SECURITY_BREACH("Security breach detected"),
        TOKEN_REFRESH("Token was refreshed"),
        MANUAL_REVOKE("Manually revoked by admin");

        private final String description;

        BlacklistReason(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
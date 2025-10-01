package org.school.management.auth.application.dto.responses;

import java.time.LocalDateTime;

public record BlacklistedTokenResponse (
        String id,
        String tokenType,
        LocalDateTime blacklistedAt,
        LocalDateTime expiresAt,
        String reason,
        String userEmail,
        boolean isExpired,
        boolean isActive
){

}


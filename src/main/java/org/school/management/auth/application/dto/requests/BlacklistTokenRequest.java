package org.school.management.auth.application.dto.requests;

import java.time.LocalDateTime;

public record BlacklistTokenRequest (
        String tokenHash,
        String tokenType,
        LocalDateTime expiresAt,
        String reason,
        String userEmail
){

}

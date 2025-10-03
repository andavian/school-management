package org.school.management.auth.application.dto.requests;

import java.time.LocalDateTime;

public record BlacklistTokenRequest (
        String token,
        String tokenType,
        LocalDateTime expiresAt,
        String reason,
        String userDni

){

   }

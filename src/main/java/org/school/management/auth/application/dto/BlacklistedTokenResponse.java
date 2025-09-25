package org.school.management.auth.application.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class BlacklistedTokenResponse {
    private String id;
    private String tokenType;
    private LocalDateTime blacklistedAt;
    private LocalDateTime expiresAt;
    private String reason;
    private String userEmail;
    private boolean isExpired;
    private boolean isActive;
}


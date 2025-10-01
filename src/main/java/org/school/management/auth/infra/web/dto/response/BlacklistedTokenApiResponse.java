package org.school.management.auth.infra.web.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BlacklistedTokenApiResponse {
    private String id;
    private String tokenType;
    private LocalDateTime blacklistedAt;
    private LocalDateTime expiresAt;
    private String reason;
    private String userEmail;
    private boolean isExpired;
    private boolean isActive;
}

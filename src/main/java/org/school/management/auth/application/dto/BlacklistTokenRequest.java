package org.school.management.auth.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BlacklistTokenRequest {
    private String tokenHash;
    private String tokenType;
    private LocalDateTime expiresAt;
    private String reason;
    private String userEmail;
}

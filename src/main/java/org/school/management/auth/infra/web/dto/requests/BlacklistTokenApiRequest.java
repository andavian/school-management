package org.school.management.auth.infra.web.dto.requests;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
public class BlacklistTokenApiRequest {
    @NotBlank(message = "Token hash is required")
    private String tokenHash;

    @NotBlank(message = "Token type is required")
    @Pattern(regexp = "ACCESS|REFRESH|CONFIRMATION", message = "Invalid token type")
    private String tokenType;

    @NotNull(message = "Expiration date is required")
    @Future(message = "Expiration date must be in the future")
    private LocalDateTime expiresAt;

    @NotBlank(message = "Reason is required")
    private String reason;
}


package org.school.management.auth.infra.web.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

// UserApiResponse.java
public record UserApiResponse(
        String userId,
        String dni,
        Set<String> roles,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime lastLoginAt,
        LocalDateTime updatedAt
) {}
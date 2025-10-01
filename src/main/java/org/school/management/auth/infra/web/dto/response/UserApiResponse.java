package org.school.management.auth.infra.web.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

// UserApiResponse.java
public record UserApiResponse(
        String userId,
        String email,
        Set<String> roles,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime lastLoginAt,
        LocalDateTime updatedAt
) {}
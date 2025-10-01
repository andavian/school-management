package org.school.management.auth.application.dto.responses;

import java.time.LocalDateTime;
import java.util.Set;


public record UserResponse (
        String userId,
        String dni,
        Set<String> roles,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime lastLoginAt,
        LocalDateTime updatedAt) {

}
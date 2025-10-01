package org.school.management.auth.application.dto.requests;

import java.time.LocalDateTime;
import java.util.Set;

public record UserSearchRequest (
        String email,
        Set<String> roles,
        Boolean isActive,
        LocalDateTime createdFrom,
        LocalDateTime createdTo,
        LocalDateTime lastLoginFrom,
        LocalDateTime lastLoginTo,
        int page,
        int size,
        String sortBy,
        String sortDirection
) {

}
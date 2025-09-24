package org.school.management.auth.application.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class UserSearchRequest {
    private String email;
    private Set<String> roles;
    private Boolean isActive;
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
    private LocalDateTime lastLoginFrom;
    private LocalDateTime lastLoginTo;
    private int page;
    private int size;
    private String sortBy;
    private String sortDirection;
}
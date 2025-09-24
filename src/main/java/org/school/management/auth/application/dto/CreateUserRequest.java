package org.school.management.auth.application.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class CreateUserRequest {
    private String email;
    private String password;
    private Set<String> roles;
}
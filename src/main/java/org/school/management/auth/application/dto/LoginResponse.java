package org.school.management.auth.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String refreshToken;
    private UserResponse user;
    private Long expiresIn;
    private String tokenType;
}
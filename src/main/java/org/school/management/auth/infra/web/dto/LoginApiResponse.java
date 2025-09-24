package org.school.management.auth.infra.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginApiResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private UserApiResponse user;
}
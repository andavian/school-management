package org.school.management.auth.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangePasswordRequest {
    private String userId;
    private String currentPassword;
    private String newPassword;
}
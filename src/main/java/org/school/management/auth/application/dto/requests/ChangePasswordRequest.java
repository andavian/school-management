package org.school.management.auth.application.dto.requests;

public record ChangePasswordRequest (
        String userId,
        String currentPassword,
        String newPassword
){

}
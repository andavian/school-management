package org.school.management.auth.application.dto.responses;


public record ChangePasswordResponse (
        boolean success,
        String message
) {

}

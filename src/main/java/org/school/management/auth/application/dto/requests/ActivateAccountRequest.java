package org.school.management.auth.application.dto.requests;


public record ActivateAccountRequest (
        String token, // Token de confirmación
        String newPassword
){

}

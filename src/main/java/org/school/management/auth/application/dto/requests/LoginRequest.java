package org.school.management.auth.application.dto.requests;


public record LoginRequest (
        String dni,
        String password
) {

}
package org.school.management.auth.application.dto.requests;


import org.school.management.auth.domain.valueobject.PlainPassword;

public record LoginRequest (
        String dni,
        String password
) {

}
package org.school.management.auth.application.dto.requests;

import java.util.Set;

public record CreateUserRequest (
        String dni,
        String password,
        Set<String> roles
) {

}
package org.school.management.auth.application.dto.requests;

import java.util.Set;

public record CreateUserRequest (
        String email,
        String password,
        Set<String> roles
) {

}
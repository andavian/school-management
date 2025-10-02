package org.school.management.auth.application.dto.responses;

public record LogoutResponse(
        boolean success,
        String message
) {}


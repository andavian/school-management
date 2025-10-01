package org.school.management.auth.infra.web.dto.response;

public record SuccessApiResponse(
        boolean success,
        String message,
        String timestamp
) {}
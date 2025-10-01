package org.school.management.auth.infra.web.dto.response;

import java.time.LocalDateTime;

public record ErrorApiResponse(
        boolean success,
        String message,
        String errorCode,
        LocalDateTime timestamp,
        String path,
        java.util.List<FieldError> fieldErrors
) {
    public record FieldError(
            String field,
            Object rejectedValue,
            String message
    ) {}
}
package org.school.management.auth.infra.web.dto.response;

public record CreateStudentApiResponse(
        String userId,
        String dni,
        String email,
        String initialPassword
) {}
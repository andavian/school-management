package org.school.management.auth.infra.web.dto.response;

public record CreateTeacherApiResponse(
        String userId,
        String email,
        String temporaryPassword,
        boolean invitationSent
) {}
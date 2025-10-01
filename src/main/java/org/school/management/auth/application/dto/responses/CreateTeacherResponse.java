package org.school.management.auth.application.dto.responses;

public record CreateTeacherResponse (
        String userId,
        String dni,
        String temporaryPassword, // Solo para demo/testin,
        boolean invitationSent
){

}

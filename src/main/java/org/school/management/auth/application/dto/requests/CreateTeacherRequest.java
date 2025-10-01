package org.school.management.auth.application.dto.requests;

public record CreateTeacherRequest (
        String dni,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String subject // Materia que enseñ,
) {

}

package org.school.management.auth.application.dto.requests;


public record CreateStudentRequest (
        String dni,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String parentEmail, // Para vincular con padr,
        String grade,
        String division
){

}

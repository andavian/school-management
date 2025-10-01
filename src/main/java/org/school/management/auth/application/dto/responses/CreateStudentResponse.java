package org.school.management.auth.application.dto.responses;


public record CreateStudentResponse (
       String userId,
       String dni,
       String initialPassword
){

}

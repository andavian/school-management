package org.school.management.auth.application.dto.responses;


public record ActivateAccountResponse (
        boolean success,
        String message
){

}
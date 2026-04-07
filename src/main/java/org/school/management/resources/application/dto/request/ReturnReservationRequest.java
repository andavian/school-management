package org.school.management.resources.application.dto.request;

import jakarta.validation.constraints.Size;

public record ReturnReservationRequest(
        @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres")
        String observations
) {}
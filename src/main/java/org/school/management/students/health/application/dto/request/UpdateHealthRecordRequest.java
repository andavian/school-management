package org.school.management.students.health.application.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateHealthRecordRequest(

        @Pattern(
                regexp = "^(A|B|AB|O)[+-]$",
                message = "Blood type must be one of: A+, A-, B+, B-, AB+, AB-, O+, O-"
        )
        String bloodType,

        @Size(max = 100, message = "Health insurance must not exceed 100 characters")
        String healthInsurance,

        @Size(max = 50, message = "Health insurance number must not exceed 50 characters")
        String healthInsuranceNumber,

        String allergies,

        String chronicConditions,

        String medications,

        String medicalObservations,

        @Size(min = 1, max = 100, message = "Emergency contact first name is required")
        String emergencyContactFirstName,

        @Size(min = 1, max = 100, message = "Emergency contact last name is required")
        String emergencyContactLastName,

        @Pattern(
                regexp = "^[+]?[0-9\\s\\-()]{7,20}$",
                message = "Invalid phone number format"
        )
        String emergencyContactPhone
) {}
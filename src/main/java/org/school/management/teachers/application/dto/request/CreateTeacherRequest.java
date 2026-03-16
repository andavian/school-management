package org.school.management.teachers.application.dto.request;

import jakarta.validation.constraints.*;
import org.school.management.teachers.domain.valueobject.EmploymentType;

import java.time.LocalDate;

public record CreateTeacherRequest(

        // Datos personales
        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name cannot exceed 100 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name cannot exceed 100 characters")
        String lastName,

        @NotBlank(message = "DNI is required")
        @Pattern(regexp = "\\d{8}", message = "DNI must be exactly 8 digits")
        String dni,

        @NotBlank(message = "CUIL is required")
        @Pattern(regexp = "\\d{11}", message = "CUIL must be exactly 11 digits")
        String cuil,

        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        @Size(max = 254, message = "Email cannot exceed 254 characters")
        String email,

        LocalDate birthDate,

        String birthPlaceId,

        @Pattern(regexp = "MALE|FEMALE|OTHER", message = "Gender must be MALE, FEMALE or OTHER")
        String gender,

        @Size(max = 100, message = "Nationality cannot exceed 100 characters")
        String nationality,

        // Contacto
        @NotBlank(message = "Phone is required")
        @Size(max = 20, message = "Phone cannot exceed 20 characters")
        String phone,

        AddressRequest address,

        // Profesional
        @Size(max = 200, message = "Specialization cannot exceed 200 characters")
        String specialization,

        @Size(max = 100, message = "Teaching license cannot exceed 100 characters")
        String teachingLicense,

        @NotNull(message = "Hire date is required")
        LocalDate hireDate,

        @NotNull(message = "Employment type is required")
        EmploymentType employmentType
) {
    public record AddressRequest(
            @Size(max = 200) String street,
            @Size(max = 10)  String number,
            @Size(max = 10)  String floor,
            @Size(max = 10)  String apartment,
            String placeId,
            @Size(max = 10)  String postalCode
    ) {}
}
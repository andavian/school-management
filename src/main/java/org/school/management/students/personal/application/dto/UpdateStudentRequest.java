package org.school.management.students.personal.application.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;


public record UpdateStudentRequest(

        @NotNull(message = "Student ID is required")
        UUID studentId,

        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "First name can only contain letters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Last name can only contain letters")
        String lastName,

        @Pattern(regexp = "^\\d{10,20}$", message = "Phone must be between 10 and 20 digits")
        String phone,

        @Email(message = "Invalid email format")
        @Size(max = 255, message = "Email cannot exceed 255 characters")
        String email,

        @NotBlank(message = "Address street is required")
        @Size(max = 200, message = "Street cannot exceed 200 characters")
        String addressStreet,

        @NotBlank(message = "Address number is required")
        @Size(max = 10, message = "Address number cannot exceed 10 characters")
        @Pattern(regexp = "[0-9]+[A-Za-z]?", message = "Invalid street number format")
        String addressNumber,

        @Size(max = 10, message = "Floor cannot exceed 10 characters")
        String addressFloor,

        @Size(max = 10, message = "Apartment cannot exceed 10 characters")
        String addressApartment,

        @Size(max = 10, message = "Postal code cannot exceed 10 characters")
        String postalCode,

        @NotNull(message = "Residence place is required")
        UUID residencePlaceId


) {

}

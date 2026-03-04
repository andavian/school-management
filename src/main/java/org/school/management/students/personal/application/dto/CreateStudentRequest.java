package org.school.management.students.personal.application.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;


public record CreateStudentRequest(

        // Datos personales
        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "First name can only contain letters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Last name can only contain letters")
        String lastName,

        @NotBlank(message = "DNI is required")
        @Pattern(regexp = "^\\d{7,8}$", message = "DNI must be 7 or 8 digits")
        String dni,

        @NotBlank(message = "CUIL is required")
        @Pattern(regexp = "^\\d{11}$", message = "CUIL must be 11 digits")
        String cuil,

        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,

        @NotBlank(message = "Gender is required")
        @Pattern(regexp = "MALE|FEMALE|OTHER", message = "Gender must be MALE, FEMALE or OTHER")
        String gender,

        @NotBlank(message = "Nationality is required")
        @Size(max = 100, message = "Nationality cannot exceed 100 characters")
        String nationality,

        // Contacto
        @Pattern(regexp = "^\\d{10,20}$", message = "Phone must be between 10 and 20 digits")
        String phone,

        @Email(message = "Invalid email format")
        @Size(max = 255, message = "Email cannot exceed 255 characters")
        String email,

        // Domicilio (según Address del Shared Kernel)
        @NotBlank(message = "Address street is required")
        @Size(max = 200, message = "Street cannot exceed 200 characters")
        String addressStreet,

        @NotBlank(message = "Address number is required")
        @Size(max = 10, message = "Address number cannot exceed 10 characters")
        @Pattern(regexp = "[0-9]+[A-Za-z]?", message = "Invalid street number format (e.g., 1234, 567B)")
        String addressNumber,

        @Size(max = 10, message = "Floor cannot exceed 10 characters")
        String addressFloor,

        @Size(max = 10, message = "Apartment cannot exceed 10 characters")
        String addressApartment,

        @Size(max = 10, message = "Postal code cannot exceed 10 characters")
        String postalCode,

        // Geografía
        @NotNull(message = "Birth place is required")
        UUID birthPlaceId,

        @NotNull(message = "Residence place is required")
        UUID residencePlaceId

) {

    public CreateStudentRequest {

        // Validar edad: entre 11 y 21 años
        if (birthDate != null) {
            LocalDate minDate = LocalDate.now().minusYears(21);
            LocalDate maxDate = LocalDate.now().minusYears(11);

            if (birthDate.isBefore(minDate) || birthDate.isAfter(maxDate)) {
                throw new IllegalArgumentException(
                        "Student must be between 11 and 21 years old"
                );
            }
        }
    }
}


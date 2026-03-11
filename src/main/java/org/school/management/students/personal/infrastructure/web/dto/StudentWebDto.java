package org.school.management.students.personal.infrastructure.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.school.management.students.personal.application.dto.response.StudentResponse;
import org.school.management.students.personal.application.dto.response.StudentSummaryResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTOs de la capa web (infrastructure/web/dto/).
 *
 * Separados de los DTOs de application para respetar las 3 capas de mappers.
 * El StudentWebMapper convierte entre estos DTOs y los DTOs de application.
 *
 * — Request DTOs: reciben JSON del cliente con validaciones Jakarta.
 * — Response DTOs: envían JSON al cliente.
 *
 * Nota: CreateStudentWebRequest duplica parcialmente CreateStudentRequest de application.
 * Esto es intencional — la capa web puede evolucionar independientemente de la application.
 * Por ejemplo: el web DTO puede tener campos extra de API versioning, headers, etc.
 */
public final class StudentWebDto {

    private StudentWebDto() {}

    // ══════════════════════════════════════════════════════════════════════
    // REQUEST DTOs
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Body para POST /api/admin/students
     */
    public record CreateStudentWebRequest(

            @NotBlank(message = "El nombre es obligatorio")
            @Size(min = 2, max = 100)
            @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede contener letras")
            String firstName,

            @NotBlank(message = "El apellido es obligatorio")
            @Size(min = 2, max = 100)
            @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El apellido solo puede contener letras")
            String lastName,

            @NotBlank(message = "El DNI es obligatorio")
            @Pattern(regexp = "^\\d{8}$", message = "El DNI debe tener exactamente 8 dígitos")
            String dni,

            @NotBlank(message = "El CUIL es obligatorio")
            @Pattern(regexp = "^\\d{11}$", message = "El CUIL debe tener 11 dígitos sin guiones")
            String cuil,

            @NotNull(message = "La fecha de nacimiento es obligatoria")
            @Past(message = "La fecha de nacimiento debe ser pasada")
            LocalDate birthDate,

            @NotNull(message = "El lugar de nacimiento es obligatorio")
            UUID birthPlaceId,

            @NotBlank(message = "El género es obligatorio")
            @Pattern(regexp = "MALE|FEMALE|OTHER")
            String gender,

            @NotBlank(message = "La nacionalidad es obligatoria")
            @Size(max = 100)
            String nationality,

            @Pattern(regexp = "^[+]?[\\d\\s\\-()]{7,20}$", message = "Teléfono inválido")
            String phone,

            @Email(message = "Formato de email inválido")
            @Size(max = 255)
            String email,

            @NotBlank(message = "La calle es obligatoria")
            @Size(max = 200)
            String addressStreet,

            @NotBlank(message = "El número es obligatorio")
            @Size(max = 10)
            @Pattern(regexp = "^[0-9]+[A-Za-z]?$", message = "Número inválido (ej: 1234 o 567B)")
            String addressNumber,

            @Size(max = 10)
            String addressFloor,

            @Size(max = 10)
            String addressApartment,

            @Size(max = 10)
            String postalCode,

            @NotNull(message = "El lugar de residencia es obligatorio")
            UUID residencePlaceId,

            @Valid
            @NotNull(message = "Los datos de salud son obligatorios")
            HealthDataWebRequest healthData,

            @Valid
            @NotNull(message = "Los datos del tutor son obligatorios")
            ParentWebRequest parent,

            @NotNull(message = "El curso es obligatorio")
            UUID gradeLevelId,

            @NotBlank(message = "El tipo de inscripción es obligatorio")
            @Pattern(regexp = "NEW|TRANSFER|REPEATING")
            String enrollmentType,

            Boolean isRepeating,

            @Size(max = 200)
            String previousSchool

    ) {
        public record HealthDataWebRequest(
                @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Grupo sanguíneo inválido")
                String bloodType,
                @Size(max = 100) String healthInsurance,
                @Size(max = 50)  String healthInsuranceNumber,
                String allergies,
                String chronicConditions,
                String medications,
                String medicalObservations,
                @NotBlank @Size(max = 100) String emergencyContactFirstName,
                @NotBlank @Size(max = 100) String emergencyContactLastName,
                @NotBlank @Pattern(regexp = "^[+]?[\\d\\s\\-()]{7,20}$") String emergencyContactPhone
        ) {}

        public record ParentWebRequest(
                @NotBlank @Size(min = 2, max = 100)
                @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$") String firstName,
                @NotBlank @Size(min = 2, max = 100)
                @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$") String lastName,
                @NotBlank @Pattern(regexp = "^\\d{8}$") String dni,
                @NotBlank @Email @Size(max = 254) String email,
                @NotBlank @Pattern(regexp = "^[+]?[\\d\\s\\-()]{7,20}$") String phone,
                @NotBlank @Pattern(regexp = "FATHER|MOTHER|GUARDIAN|OTHER") String relationship,
                Boolean isPrimaryContact,
                Boolean isAuthorizedPickup
        ) {}
    }

    /**
     * Body para PATCH /api/admin/students/{id}
     * studentId va en @PathVariable — no en el body.
     */
    public record UpdateStudentWebRequest(

            @NotBlank(message = "El nombre es obligatorio")
            @Size(min = 2, max = 100)
            @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")
            String firstName,

            @NotBlank(message = "El apellido es obligatorio")
            @Size(min = 2, max = 100)
            @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")
            String lastName,

            @Pattern(regexp = "^[+]?[\\d\\s\\-()]{7,20}$")
            String phone,

            @Email
            @Size(max = 255)
            String email,

            @NotBlank @Size(max = 200) String addressStreet,
            @NotBlank @Size(max = 10)
            @Pattern(regexp = "^[0-9]+[A-Za-z]?$") String addressNumber,
            @Size(max = 10) String addressFloor,
            @Size(max = 10) String addressApartment,
            @Size(max = 10) String postalCode,

            @NotNull(message = "El lugar de residencia es obligatorio")
            UUID residencePlaceId

    ) {}

    // ══════════════════════════════════════════════════════════════════════
    // RESPONSE DTOs
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Respuesta completa — usada en POST y GET /students/{id}.
     * Envuelve StudentResponse de application añadiendo metadatos de API si fuera necesario.
     * Por ahora es una proyección directa — el web mapper copia campo a campo.
     */
    public record StudentWebResponse(
            UUID studentId,
            UUID userId,
            String dni,
            String cuil,
            String firstName,
            String lastName,
            String fullName,
            LocalDate birthDate,
            int age,
            boolean isAdult,
            String gender,
            String nationality,
            String phone,
            String email,
            AddressWebResponse address,
            PlaceWebResponse birthPlace,
            PlaceWebResponse residencePlace,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public record AddressWebResponse(
                String street,
                String number,
                String floor,
                String apartment,
                String postalCode,
                UUID residencePlaceId,
                String formatted
        ) {}

        public record PlaceWebResponse(
                UUID placeId,
                String placeName,
                String provinceName,
                String countryName
        ) {}
    }

    /**
     * Respuesta resumida — usada en GET /students (listado y búsquedas).
     */
    public record StudentSummaryWebResponse(
            UUID studentId,
            String dni,
            String fullName,
            int age,
            String email,
            String phone
    ) {}

    /**
     * Wrapper de lista paginada para búsquedas.
     */
    public record StudentSearchWebResponse(
            List<StudentSummaryWebResponse> students,
            int total
    ) {}
}
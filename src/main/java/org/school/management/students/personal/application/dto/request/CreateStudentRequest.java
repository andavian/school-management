package org.school.management.students.personal.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO de entrada para la creación completa de un estudiante.
 * Contiene datos personales + salud + padre/tutor + matrícula
 * para soportar el flujo transaccional de 15 pasos.
 *
 * Nota: la validación de reglas de negocio (edad, CUIL↔DNI, etc.)
 * se realiza en StudentPersonalData.create() — aquí solo formato.
 */
public record CreateStudentRequest(

        // ── Identidad civil ──────────────────────────────────────────────
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede contener letras")
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El apellido solo puede contener letras")
        String lastName,

        @NotBlank(message = "El DNI es obligatorio")
        @Pattern(regexp = "^\\d{8}$", message = "El DNI debe tener exactamente 8 dígitos")
        String dni,

        @NotBlank(message = "El CUIL es obligatorio")
        @Pattern(regexp = "^\\d{11}$", message = "El CUIL debe tener 11 dígitos sin guiones")
        String cuil,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
        LocalDate birthDate,

        @NotNull(message = "El lugar de nacimiento es obligatorio")
        UUID birthPlaceId,

        @NotBlank(message = "El género es obligatorio")
        @Pattern(regexp = "MALE|FEMALE|OTHER", message = "El género debe ser MALE, FEMALE u OTHER")
        String gender,

        @NotBlank(message = "La nacionalidad es obligatoria")
        @Size(max = 100, message = "La nacionalidad no puede superar los 100 caracteres")
        String nationality,

        // ── Contacto (opcionales — estudiantes menores pueden no tener) ──
        @Pattern(regexp = "^[+]?[\\d\\s\\-()]{7,20}$", message = "Teléfono inválido")
        String phone,

        @Email(message = "Formato de email inválido")
        @Size(max = 255, message = "El email no puede superar los 255 caracteres")
        String email,

        // ── Domicilio (Address del Shared Kernel) ────────────────────────
        @NotBlank(message = "La calle es obligatoria")
        @Size(max = 200, message = "La calle no puede superar los 200 caracteres")
        String addressStreet,

        @NotBlank(message = "El número de calle es obligatorio")
        @Size(max = 10, message = "El número no puede superar los 10 caracteres")
        @Pattern(regexp = "^[0-9]+[A-Za-z]?$", message = "Número inválido (ej: 1234 o 567B)")
        String addressNumber,

        @Size(max = 10, message = "El piso no puede superar los 10 caracteres")
        String addressFloor,

        @Size(max = 10, message = "El departamento no puede superar los 10 caracteres")
        String addressApartment,

        @Size(max = 10, message = "El código postal no puede superar los 10 caracteres")
        String postalCode,

        @NotNull(message = "El lugar de residencia es obligatorio")
        UUID residencePlaceId,

        // ── Datos de salud (StudentHealthRecord) ─────────────────────────
        @Valid
        @NotNull(message = "Los datos de salud son obligatorios")
        HealthDataRequest healthData,

        // ── Padre / Tutor (Parent + StudentParent) ────────────────────────
        @Valid
        @NotNull(message = "Los datos del tutor son obligatorios")
        ParentRequest parent,

        // ── Matrícula (StudentEnrollment) ─────────────────────────────────
        @NotNull(message = "El curso es obligatorio")
        UUID gradeLevelId,

        @NotBlank(message = "El tipo de inscripción es obligatorio")
        @Pattern(regexp = "NEW|TRANSFER|REPEATING", message = "Tipo inválido: NEW, TRANSFER o REPEATING")
        String enrollmentType,

        Boolean isRepeating,

        @Size(max = 200, message = "El nombre de la escuela anterior no puede superar los 200 caracteres")
        String previousSchool

) {

    // ── Nested records ────────────────────────────────────────────────────

    public record HealthDataRequest(

            @Size(max = 5, message = "El grupo sanguíneo no puede superar los 5 caracteres")
            String bloodType,

            @Size(max = 100, message = "La obra social no puede superar los 100 caracteres")
            String healthInsurance,

            @Size(max = 50, message = "El número de obra social no puede superar los 50 caracteres")
            String healthInsuranceNumber,

            String allergies,
            String chronicConditions,
            String medications,
            String medicalObservations,

            @NotBlank(message = "El nombre del contacto de emergencia es obligatorio")
            @Size(max = 100)
            String emergencyContactFirstName,

            @NotBlank(message = "El apellido del contacto de emergencia es obligatorio")
            @Size(max = 100)
            String emergencyContactLastName,

            @NotBlank(message = "El teléfono del contacto de emergencia es obligatorio")
            @Pattern(regexp = "^[+]?[\\d\\s\\-()]{7,20}$", message = "Teléfono de emergencia inválido")
            String emergencyContactPhone

    ) {}

    public record ParentRequest(

            @NotBlank(message = "El nombre del tutor es obligatorio")
            @Size(min = 2, max = 100)
            @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Solo letras")
            String firstName,

            @NotBlank(message = "El apellido del tutor es obligatorio")
            @Size(min = 2, max = 100)
            @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Solo letras")
            String lastName,

            @NotBlank(message = "El DNI del tutor es obligatorio")
            @Pattern(regexp = "^\\d{8}$", message = "El DNI debe tener exactamente 8 dígitos")
            String dni,

            @NotBlank(message = "El email del tutor es obligatorio")
            @Email(message = "Formato de email inválido")
            @Size(max = 254)
            String email,

            @NotBlank(message = "El teléfono del tutor es obligatorio")
            @Pattern(regexp = "^[+]?[\\d\\s\\-()]{7,20}$", message = "Teléfono inválido")
            String phone,

            @NotBlank(message = "La relación es obligatoria")
            @Pattern(regexp = "FATHER|MOTHER|GUARDIAN|OTHER", message = "Relación inválida")
            String relationship,

            Boolean isPrimaryContact,
            Boolean isAuthorizedPickup

    ) {}
}
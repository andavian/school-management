package org.school.management.students.personal.application.dto.request;

import jakarta.validation.constraints.*;

import java.util.UUID;

/**
 * DTO para actualizar datos de contacto y domicilio del estudiante.
 * El studentId va como @PathVariable en el controller — no en el body.
 * DNI/CUIL son inmutables — no se pueden modificar por este endpoint.
 */
public record UpdateStudentRequest(

        // ── Nombre (puede corregirse por error tipográfico) ──────────────
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede contener letras")
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El apellido solo puede contener letras")
        String lastName,

        // ── Contacto ─────────────────────────────────────────────────────
        @Pattern(regexp = "^[+]?[\\d\\s\\-()]{7,20}$", message = "Teléfono inválido")
        String phone,

        @Email(message = "Formato de email inválido")
        @Size(max = 255, message = "El email no puede superar los 255 caracteres")
        String email,

        // ── Domicilio ─────────────────────────────────────────────────────
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
        UUID residencePlaceId

) {}
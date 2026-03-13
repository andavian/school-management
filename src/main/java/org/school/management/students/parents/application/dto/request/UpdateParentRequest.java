package org.school.management.students.parents.application.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request PATCH para actualizar datos del padre.
 * Campos null conservan su valor actual (semántica PATCH).
 * DNI no es actualizable — es el identificador global.
 */
public record UpdateParentRequest(

        // ── Datos personales ──────────────────────────────────────────────
        @Size(min = 2, max = 100)
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Solo letras")
        String firstName,

        @Size(min = 2, max = 100)
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Solo letras")
        String lastName,

        @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
        LocalDate birthDate,

        @Pattern(regexp = "MALE|FEMALE|OTHER", message = "Género inválido")
        String gender,

        @Size(max = 100)
        String nationality,

        // ── Contacto ──────────────────────────────────────────────────────
        @Email(message = "Formato de email inválido")
        @Size(max = 254)
        String email,

        @Pattern(regexp = "^[+]?[\\d\\s\\-()]{7,20}$", message = "Teléfono inválido")
        String phone,

        @Pattern(regexp = "^[+]?[\\d\\s\\-()]{7,20}$", message = "Teléfono alternativo inválido")
        String phoneAlt,

        // ── Domicilio ─────────────────────────────────────────────────────
        @Size(max = 200)
        String addressStreet,

        @Size(max = 10)
        String addressNumber,

        @Size(max = 10)
        String addressFloor,

        @Size(max = 10)
        String addressApartment,

        UUID residencePlaceId,

        @Size(max = 10)
        String postalCode,

        // ── Información laboral ───────────────────────────────────────────
        @Size(max = 100)
        String occupation,

        @Size(max = 200)
        String workplace,

        @Pattern(regexp = "^[+]?[\\d\\s\\-()]{7,20}$", message = "Teléfono laboral inválido")
        String workplacePhone
) {}
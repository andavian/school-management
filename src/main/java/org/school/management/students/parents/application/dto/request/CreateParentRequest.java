package org.school.management.students.parents.application.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request para crear un nuevo padre/tutor.
 * El email es obligatorio — necesario para credenciales y notificaciones.
 */
public record CreateParentRequest(

        // ── Identidad ─────────────────────────────────────────────────────
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100)
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Solo letras")
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 2, max = 100)
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Solo letras")
        String lastName,

        @NotBlank(message = "El DNI es obligatorio")
        @Pattern(regexp = "^\\d{8}$", message = "El DNI debe tener exactamente 8 dígitos")
        String dni,

        @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
        LocalDate birthDate,

        @Pattern(regexp = "MALE|FEMALE|OTHER", message = "Género inválido")
        String gender,

        @Size(max = 100)
        String nationality,

        // ── Contacto ──────────────────────────────────────────────────────
        @NotBlank(message = "El email es obligatorio para padres")
        @Email(message = "Formato de email inválido")
        @Size(max = 254)
        String email,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^[+]?[\\d\\s\\-()]{7,20}$", message = "Teléfono inválido")
        String phone,

        @Pattern(regexp = "^[+]?[\\d\\s\\-()]{7,20}$", message = "Teléfono alternativo inválido")
        String phoneAlt,

        // ── Domicilio ─────────────────────────────────────────────────────
        @Size(max = 200)
        String addressStreet,

        @Size(max = 10)
        @Pattern(regexp = "^[0-9]+[A-Za-z]?$", message = "Número inválido")
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
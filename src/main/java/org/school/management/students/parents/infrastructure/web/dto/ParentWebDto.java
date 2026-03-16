package org.school.management.students.parents.infrastructure.web.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Clase contenedora de todos los Web DTOs del módulo parents.
 * Patrón consistente con StudentWebDto, HealthRecordWebDto,
 * EnrollmentWebDto y RecordWebDto.
 */
public final class ParentWebDto {

    private ParentWebDto() {}

    // ── Requests ──────────────────────────────────────────────────────────

    public record CreateParentWebRequest(

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

            @NotBlank(message = "El CUIL es obligatorio")               // ← NUEVO
            @Pattern(regexp = "\\d{11}", message = "CUIL debe tener exactamente 11 dígitos")
            String cuil,

            @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
            LocalDate birthDate,

            @Pattern(regexp = "MALE|FEMALE|OTHER", message = "Género inválido")
            String gender,

            @Size(max = 100)
            String nationality,

            @NotBlank(message = "El email es obligatorio para padres")
            @Email(message = "Formato de email inválido")
            @Size(max = 254)
            String email,

            @NotBlank(message = "El teléfono es obligatorio")
            @Pattern(regexp = "^[+]?[\\d\\s\\-()]{7,20}$", message = "Teléfono inválido")
            String phone,

            @Pattern(regexp = "^[+]?[\\d\\s\\-()]{7,20}$", message = "Teléfono alternativo inválido")
            String phoneAlt,

            @Size(max = 200)
            String addressStreet,

            @Size(max = 10)
            String addressNumber,

            @Size(max = 10)
            String addressFloor,

            @Size(max = 10)
            String addressApartment,

            UUID placeId,

            @Size(max = 10)
            String postalCode,

            @Size(max = 100)
            String occupation,

            @Size(max = 200)
            String workplace,

            @Pattern(regexp = "^[+]?[\\d\\s\\-()]{7,20}$", message = "Teléfono laboral inválido")
            String workplacePhone

    ) {}

    public record UpdateParentWebRequest(

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

            @Email(message = "Formato de email inválido")
            @Size(max = 254)
            String email,

            @Pattern(regexp = "^[+]?[\\d\\s\\-()]{7,20}$", message = "Teléfono inválido")
            String phone,

            @Pattern(regexp = "^[+]?[\\d\\s\\-()]{7,20}$", message = "Teléfono alternativo inválido")
            String phoneAlt,

            @Size(max = 200)
            String addressStreet,

            @Size(max = 10)
            String addressNumber,

            @Size(max = 10)
            String addressFloor,

            @Size(max = 10)
            String addressApartment,

            UUID placeId,

            @Size(max = 10)
            String postalCode,

            @Size(max = 100)
            String occupation,

            @Size(max = 200)
            String workplace,

            @Pattern(regexp = "^[+]?[\\d\\s\\-()]{7,20}$", message = "Teléfono laboral inválido")
            String workplacePhone

    ) {}

    public record LinkParentWebRequest(

            @NotBlank(message = "El DNI del padre es obligatorio")
            @Pattern(regexp = "^\\d{8}$", message = "El DNI debe tener exactamente 8 dígitos")
            String parentDni,

            @NotBlank(message = "La relación es obligatoria")
            @Pattern(
                    regexp = "FATHER|MOTHER|GUARDIAN|GRANDPARENT|SIBLING|OTHER",
                    message = "Relación inválida"
            )
            String relationship,

            Boolean isPrimaryContact,
            Boolean isAuthorizedPickup,
            Boolean isEmergencyContact,

            @Size(max = 500)
            String notes

    ) {}

    // ── Responses ─────────────────────────────────────────────────────────

    public record ParentWebResponse(
            UUID parentId,
            UUID userId,
            String dni,
            String cuil,
            String firstName,
            String lastName,
            String fullName,
            LocalDate birthDate,
            String gender,
            String nationality,
            String email,
            String phone,
            String phoneAlt,
            String addressStreet,
            String addressNumber,
            String addressFloor,
            String addressApartment,
            UUID placeId,
            String postalCode,
            String occupation,
            String workplace,
            String workplacePhone,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    public record StudentParentWebResponse(
            UUID studentParentId,
            UUID studentId,
            UUID parentId,
            String relationship,
            String relationshipDisplay,
            boolean primaryContact,
            boolean authorizedPickup,
            boolean emergencyContact,
            String notes,
            ParentWebResponse parent,
            LocalDateTime createdAt
    ) {}

    public record ParentSummaryWebResponse(
            UUID parentId,
            String dni,
            String fullName,
            String email,
            String phone,
            boolean active
    ) {}
}
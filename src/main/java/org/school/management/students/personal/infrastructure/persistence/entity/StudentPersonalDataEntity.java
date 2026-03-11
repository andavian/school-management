package org.school.management.students.personal.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;
import org.school.management.shared.person.domain.valueobject.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA para student_personal_data.
 *
 * — PK y FKs mapeados como BINARY(16) via UuidBinaryConverter.
 * — Gender usa el enum del Shared Kernel directamente (no GenderEntity duplicado).
 * — Address aplanada: addressStreet, addressNumber, addressFloor, addressApartment,
 *   residencePlaceId, postalCode — exactamente las columnas de V10.
 * — Sin lógica de negocio — solo persistencia.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "student_personal_data")
public class StudentPersonalDataEntity {

    // ── PK — UUID con conversión transparente BINARY(16) ↔ UUID ──────────
    // JpaRepository<StudentPersonalDataEntity, UUID> — findById(UUID) funciona directo.

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "student_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID studentId;

    // ── FK: auth.users ────────────────────────────────────────────────────

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "user_id", columnDefinition = "BINARY(16)", nullable = false, unique = true)
    private UUID userId;

    // ── Identidad civil ───────────────────────────────────────────────────

    @Column(name = "dni", nullable = false, unique = true, length = 8)
    private String dni;

    @Column(name = "cuil", nullable = false, unique = true, length = 11)
    private String cuil;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "birth_place_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID birthPlaceId;

    /**
     * Usa Gender del Shared Kernel directamente — evita duplicar GenderEntity.
     * Se persiste como String (MALE / FEMALE / OTHER).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    @Column(name = "nationality", nullable = false, length = 100)
    private String nationality;

    // ── Contacto (nullable — estudiantes menores) ─────────────────────────

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 255)
    private String email;

    // ── Domicilio (Address aplanada) ──────────────────────────────────────

    @Column(name = "address_street", nullable = false, length = 200)
    private String addressStreet;

    @Column(name = "address_number", nullable = false, length = 10)
    private String addressNumber;

    @Column(name = "address_floor", length = 10)
    private String addressFloor;

    @Column(name = "address_apartment", length = 10)
    private String addressApartment;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "residence_place_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID residencePlaceId;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    // ── Auditoría ─────────────────────────────────────────────────────────

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "created_by", columnDefinition = "BINARY(16)", nullable = false)
    private UUID createdBy;

    // ── Lifecycle callbacks ───────────────────────────────────────────────

    /**
     * Garantiza que createdAt y updatedAt nunca lleguen null a la BD,
     * incluso si el dominio no los setea explícitamente.
     */
    @PrePersist
    protected void onPrePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    protected void onPreUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
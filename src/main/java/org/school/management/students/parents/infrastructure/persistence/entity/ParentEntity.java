package org.school.management.students.parents.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;
import org.school.management.shared.person.domain.valueobject.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "parents")
public class ParentEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "parent_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID parentId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "user_id", columnDefinition = "BINARY(16)", nullable = false, unique = true)
    private UUID userId;

    // Datos personales
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "dni", nullable = false, length = 8, unique = true)
    private String dni;

    @Column(name = "cuil", nullable = false, unique = true, length = 11)
    private String cuil;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Column(name = "nationality", length = 100)
    private String nationality;

    // Contacto
    @Column(name = "email", nullable = false, length = 254, unique = true)
    private String email;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "phone_alt", length = 20)
    private String phoneAlt;

    // Domicilio
    @Column(name = "address_street", length = 200)
    private String addressStreet;

    @Column(name = "address_number", length = 10)
    private String addressNumber;

    @Column(name = "address_floor", length = 10)
    private String addressFloor;

    @Column(name = "address_apartment", length = 10)
    private String addressApartment;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "place_id", columnDefinition = "BINARY(16)")
    private UUID placeId;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    // Información laboral
    @Column(name = "occupation", length = 100)
    private String occupation;

    @Column(name = "workplace", length = 200)
    private String workplace;

    @Column(name = "workplace_phone", length = 20)
    private String workplacePhone;

    // Estado
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    // Auditoría
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "created_by", columnDefinition = "BINARY(16)", nullable = false)
    private UUID createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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
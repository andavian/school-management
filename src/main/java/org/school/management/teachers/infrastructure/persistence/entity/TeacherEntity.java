package org.school.management.teachers.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;
import org.school.management.shared.person.domain.valueobject.Gender;
import org.school.management.teachers.domain.valueobject.EmploymentStatus;
import org.school.management.teachers.domain.valueobject.EmploymentType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "teachers")
public class TeacherEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "teacher_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID teacherId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "user_id", columnDefinition = "BINARY(16)", nullable = false, unique = true)
    private UUID userId;

    // Datos personales
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "dni", nullable = false, unique = true, length = 8)
    private String dni;

    @Column(name = "cuil", nullable = false, unique = true, length = 11)
    private String cuil;

    @Column(name = "email", nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "birth_place_id", columnDefinition = "BINARY(16)")
    private UUID birthPlaceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Column(name = "nationality", length = 100)
    private String nationality;

    // Contacto
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    // Domicilio (Address aplanada — columna place_id igual que parents)
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

    // Información profesional
    @Column(name = "specialization", length = 200)
    private String specialization;

    @Column(name = "teaching_license", length = 100)
    private String teachingLicense;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", nullable = false, length = 20)
    private EmploymentStatus employmentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false, length = 20)
    private EmploymentType employmentType;

    // Estado de cuenta
    @Column(name = "is_active", nullable = false)
    private boolean isActive = false;

    @Column(name = "activation_token", length = 64)
    private String activationToken;

    @Column(name = "activation_sent_at")
    private LocalDateTime activationSentAt;

    @Column(name = "activated_at")
    private LocalDateTime activatedAt;

    // Auditoría
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "created_by", columnDefinition = "BINARY(16)", nullable = false)
    private UUID createdBy;

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
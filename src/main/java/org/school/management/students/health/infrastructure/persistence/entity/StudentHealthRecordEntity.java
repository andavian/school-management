package org.school.management.students.health.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA para student_health_records.
 *
 * Notas de mapeo:
 * - emergency_contact_name almacena "firstName lastName" concatenado.
 *   La separación se realiza en StudentHealthRecordPersistenceMapper via @AfterMapping.
 * - bloodType almacena el nombre del enum (A_POSITIVE, etc.) — no el displayName.
 *   La conversión a/desde displayName se hace en el PersistenceMapper.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "student_health_records")
public class StudentHealthRecordEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "health_record_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID healthRecordId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "student_id", columnDefinition = "BINARY(16)", nullable = false, unique = true)
    private UUID studentId;

    // Datos médicos
    @Column(name = "blood_type", length = 5)
    private String bloodType;

    @Column(name = "health_insurance", length = 100)
    private String healthInsurance;

    @Column(name = "health_insurance_number", length = 50)
    private String healthInsuranceNumber;

    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "chronic_conditions", columnDefinition = "TEXT")
    private String chronicConditions;

    @Column(name = "medications", columnDefinition = "TEXT")
    private String medications;

    @Column(name = "medical_observations", columnDefinition = "TEXT")
    private String medicalObservations;

    // Contacto de emergencia
    // Columna única: almacena "firstName lastName" — se separa en el PersistenceMapper
    @Column(name = "emergency_contact_name", length = 200, nullable = false)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 20, nullable = false)
    private String emergencyContactPhone;

    // Auditoría
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
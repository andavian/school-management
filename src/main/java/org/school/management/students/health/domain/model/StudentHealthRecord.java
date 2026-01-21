package org.school.management.students.health.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.school.management.shared.person.domain.valueobject.FullName;
import org.school.management.shared.person.domain.valueobject.PhoneNumber;
import org.school.management.students.health.domain.valueobject.BloodType;
import org.school.management.students.health.domain.valueobject.HealthRecordId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Agregado Root: Ficha Médica del Estudiante
 *
 * Responsabilidad: Gestionar información de salud y emergencias
 * Solo referencia StudentId, no conoce PersonalData completo
 */
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class StudentHealthRecord {

    // Identidad
    @EqualsAndHashCode.Include
    private final HealthRecordId healthRecordId;
    private final StudentPersonalDataId studentId;

    // Datos médicos básicos
    private final BloodType bloodType;

    // Obra social / Seguro médico
    private final String healthInsurance;
    private final String healthInsuranceNumber;

    // Condiciones médicas
    private final String allergies;
    private final String chronicConditions;
    private final String medications;
    private final String medicalObservations;

    // Contacto de emergencia (obligatorio)
    private final FullName emergencyContactName;
    private final PhoneNumber emergencyContactPhone;

    // Auditoría
    @Builder.Default
    private final LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private final LocalDateTime updatedAt = LocalDateTime.now();



    // ============ Domain Logic ============

    /**
     * Valida si tiene información médica completa
     */
    public boolean hasCompleteHealthInfo() {
        return bloodType != null &&
                healthInsurance != null && !healthInsurance.isBlank() &&
                healthInsuranceNumber != null && !healthInsuranceNumber.isBlank();
    }

    /**
     * Valida si tiene alergias registradas
     */
    public boolean hasAllergies() {
        return allergies != null && !allergies.isBlank();
    }

    /**
     * Valida si tiene condiciones crónicas registradas
     */
    public boolean hasChronicConditions() {
        return chronicConditions != null && !chronicConditions.isBlank();
    }

    /**
     * Valida si requiere medicación
     */
    public boolean requiresMedication() {
        return medications != null && !medications.isBlank();
    }

    /**
     * Obtiene resumen de alertas médicas (para mostrar en UI)
     */
    public String getMedicalAlertsSummary() {
        var alerts = new StringBuilder();

        if (hasAllergies()) {
            alerts.append("⚠️ ALERGIAS: ").append(allergies).append(" | ");
        }
        if (hasChronicConditions()) {
            alerts.append("🏥 CONDICIÓN: ").append(chronicConditions).append(" | ");
        }
        if (requiresMedication()) {
            alerts.append("💊 MEDICACIÓN: ").append(medications);
        }

        return alerts.length() > 0 ? alerts.toString() : "Sin alertas médicas";
    }

    // Factory method para encapsular lógica de creación
    public static StudentHealthRecord create(StudentHealthRecordBuilder builder) {
        Objects.requireNonNull(builder.healthRecordId, "HealthRecordId cannot be null");
        Objects.requireNonNull(builder.studentId, "StudentId cannot be null");

        if (builder.emergencyContactName == null) {
            throw new IllegalArgumentException("Emergency contact name is mandatory");
        }
        Objects.requireNonNull(builder.emergencyContactPhone, "Emergency contact phone is mandatory");

        return builder.build();
    }
}
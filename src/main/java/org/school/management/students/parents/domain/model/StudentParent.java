package org.school.management.students.parents.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.school.management.students.parents.domain.exception.InvalidParentDataException;
import org.school.management.students.parents.domain.valueobject.ParentId;
import org.school.management.students.parents.domain.valueobject.ParentRelationship;
import org.school.management.students.parents.domain.valueobject.StudentParentId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Agregado Root: Vínculo Estudiante — Padre/Tutor
 *
 * Representa la relación entre un estudiante y un padre/tutor.
 * Contiene los flags operativos que son específicos de esta relación
 * y no del padre en sí (un padre puede ser contacto principal
 * de un hijo pero no de otro).
 *
 * Reglas de negocio:
 * - La combinación studentId + parentId es única
 * - isPrimaryContact es exclusivo por estudiante (validado en use case)
 * - Un estudiante debe tener al menos un contacto de emergencia
 */
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class StudentParent {

    @EqualsAndHashCode.Include
    private final StudentParentId studentParentId;

    private final StudentPersonalDataId studentId;
    private final ParentId parentId;

    // Tipo de relación
    private final ParentRelationship relationship;

    // Flags operativos — específicos de esta relación
    @Builder.Default
    private boolean isPrimaryContact = false;

    @Builder.Default
    private boolean isAuthorizedPickup = true;

    @Builder.Default
    private boolean isEmergencyContact = true;

    private String notes;

    // Auditoría
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // ============ Domain Logic ============

    /**
     * Designa este vínculo como contacto principal del estudiante.
     */
    public void markAsPrimaryContact() {
        this.isPrimaryContact = true;
    }

    /**
     * Quita la designación de contacto principal.
     */
    public void removePrimaryContact() {
        this.isPrimaryContact = false;
    }

    /**
     * Actualiza los flags operativos del vínculo.
     */
    public void updateFlags(
            boolean isPrimaryContact,
            boolean isAuthorizedPickup,
            boolean isEmergencyContact,
            String notes) {

        this.isPrimaryContact = isPrimaryContact;
        this.isAuthorizedPickup = isAuthorizedPickup;
        this.isEmergencyContact = isEmergencyContact;
        this.notes = notes;
    }

    // Factory method
    public static StudentParent create(StudentParentBuilder builder) {
        Objects.requireNonNull(builder.studentParentId, "StudentParentId cannot be null");
        Objects.requireNonNull(builder.studentId,       "StudentId cannot be null");
        Objects.requireNonNull(builder.parentId,        "ParentId cannot be null");
        Objects.requireNonNull(builder.relationship,    "Relationship cannot be null");

        return builder.build();
    }
}
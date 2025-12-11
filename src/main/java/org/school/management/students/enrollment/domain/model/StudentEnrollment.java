// src/main/java/org/school/management/students/enrollment/domain/model/StudentEnrollment.java
package org.school.management.students.enrollment.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.academic.domain.valueobject.ids.RegistryId;
import org.school.management.shared.person.domain.valueobject.*;
import org.school.management.students.enrollment.domain.exception.*;
import org.school.management.students.enrollment.domain.valueobject.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudentEnrollment {

    // Identidad propia del contexto students
    private EnrollmentId enrollmentId;

    // Referencia externa al usuario (anti-corruption layer)
    private UserId userId;

    // Datos de persona (shared kernel – acoplamiento permitido y deseado)
    private DNI dni;
    private FullName fullName;
    private LocalDate birthDate;
    private Gender gender;
    private BirthPlaceId birthPlaceId;

    // Datos específicos de matrícula
    private EnrollmentStatus status;
    private LocalDate enrollmentDate;
    private LocalDate withdrawalDate;
    private String withdrawalReason;

    private FolioNumber folioNumber;
    private RegistryId registryId; // referencia al libro matriz

    // Auditoría
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserId createdBy;

    // ==================== FACTORY ====================

    public static StudentEnrollment enrollNewStudent(
            UserId userId,
            DNI dni,
            FullName fullName,
            LocalDate birthDate,
            Gender gender,
            BirthPlaceId birthPlaceId,
            FolioNumber folioNumber,
            RegistryId registryId,
            UserId createdBy) {

        if (dni == null) throw new IllegalArgumentException("DNI es obligatorio");

        var enrollment = new StudentEnrollment();
        enrollment.enrollmentId = EnrollmentId.generate();
        enrollment.userId = userId;
        enrollment.dni = dni;
        enrollment.fullName = fullName;
        enrollment.birthDate = birthDate;
        enrollment.gender = gender;
        enrollment.birthPlaceId = birthPlaceId != null ? birthPlaceId : BirthPlaceId.of("argentina-default-id");
        enrollment.folioNumber = folioNumber;
        enrollment.registryId = registryId;

        enrollment.status = EnrollmentStatus.ACTIVE;
        enrollment.enrollmentDate = LocalDate.now();
        enrollment.createdAt = LocalDateTime.now();
        enrollment.updatedAt = LocalDateTime.now();
        enrollment.createdBy = createdBy;

        return enrollment;
    }

    // ==================== COMPORTAMIENTO RICO ====================

    public StudentEnrollment withdraw(String reason, UserId withdrawnBy) {
        if (this.status != EnrollmentStatus.ACTIVE) {
            throw new StudentNotActiveException(this.enrollmentId);
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo de baja es obligatorio");
        }

        this.status = EnrollmentStatus.WITHDRAWN;
        this.withdrawalDate = LocalDate.now();
        this.withdrawalReason = reason.trim();
        this.updatedAt = LocalDateTime.now();

        // Aquí lanzarías un Domain Event → StudentWithdrawnEvent
        return this;
    }

    public boolean isActive() {
        return this.status == EnrollmentStatus.ACTIVE;
    }

    public boolean hasSameIdentityAs(StudentEnrollment other) {
        return this.dni.equals(other.dni);
    }

    // Solo para uso interno de infraestructura (JPA)
    void setEnrollmentId(EnrollmentId id) { this.enrollmentId = id; }
}
package org.school.management.students.domain.model;

import lombok.Builder;
import lombok.Value;
import org.school.management.academic.domain.valueobject.ids.RegistryId;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.geography.domain.valueobject.PlaceId;
import org.school.management.parents.domain.valueobject.StudentId;
import org.school.management.students.enrollment.domain.valueobject.EnrollmentStatus;
import org.school.management.students.enrollment.domain.valueobject.FolioNumber;
import org.school.management.shared.person.domain.valueobject.FullName;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
@Builder
public class Student {
    StudentId studentId;
    UserId userId;

    // Datos personales
    FullName fullName;
    LocalDate birthDate;
    PlaceId birthPlaceId;
    Gender gender;
    String nationality;

    // Documentación
    String dniFrontImage;
    String dniBackImage;
    String profilePhoto;

    // Contacto
    String phone;
    Address address;

    // Salud
    BloodType bloodType;
    HealthInsurance healthInsurance;
    String allergies;
    String medicalObservations;

    // Registro de calificaciones
    RegistryId registryId;
    FolioNumber folioNumber;

    // Estado
    EnrollmentStatus enrollmentStatus;
    LocalDate enrollmentDate;
    LocalDate withdrawalDate;
    String withdrawalReason;

    // Auditoría
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    UserId createdBy;

    // Factory method
    public static Student createNew(
            UserId userId,
            FullName fullName,
            LocalDate birthDate,
            PlaceId birthPlaceId,
            Gender gender,
            Address address,
            RegistryId registryId,
            FolioNumber folioNumber,
            UserId createdBy
    ) {
        return Student.builder()
                .studentId(StudentId.generate())
                .userId(userId)
                .fullName(fullName)
                .birthDate(birthDate)
                .birthPlaceId(birthPlaceId)
                .gender(gender)
                .nationality("Argentina")
                .address(address)
                .registryId(registryId)
                .folioNumber(folioNumber)
                .enrollmentStatus(EnrollmentStatus.ACTIVE)
                .enrollmentDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();
    }

    public Student updateProfile(
            String phone,
            Address address,
            BloodType bloodType,
            HealthInsurance healthInsurance
    ) {
        return this.toBuilder()
                .phone(phone)
                .address(address)
                .bloodType(bloodType)
                .healthInsurance(healthInsurance)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Student withdraw(String reason, UserId withdrawnBy) {
        if (this.enrollmentStatus != EnrollmentStatus.ACTIVE) {
            throw new IllegalStateException("Student is not active");
        }

        return this.toBuilder()
                .enrollmentStatus(EnrollmentStatus.WITHDRAWN)
                .withdrawalDate(LocalDate.now())
                .withdrawalReason(reason)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public boolean canEnrollInGradeLevel(int yearLevel) {
        // Lógica de validación de inscripción
        return this.enrollmentStatus == EnrollmentStatus.ACTIVE;
    }
}
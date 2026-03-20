package org.school.management.teachers.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.geography.domain.valueobject.PlaceId;
import org.school.management.shared.person.domain.valueobject.*;
import org.school.management.teachers.domain.valueobject.EmploymentStatus;
import org.school.management.teachers.domain.valueobject.EmploymentType;
import org.school.management.teachers.domain.valueobject.TeacherId;
import org.school.management.teachers.domain.valueobject.TeacherSpecialization;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Teacher {

    @EqualsAndHashCode.Include
    private final TeacherId teacherId;
    private final UserId userId;

    // Datos personales — Shared Kernel
    private FullName fullName;
    private final Dni dni;
    private final Cuil cuil;
    private Email email;
    private LocalDate birthDate;
    private PlaceId birthPlaceId;
    private Gender gender;
    private Nationality nationality;

    // Contacto — mutable via updateContactInfo()
    private PhoneNumber phone;
    private Address address;

    // Profesional — mutable via updateProfessionalInfo()
    private TeacherSpecialization specialization;
    private String teachingLicense;
    private final LocalDate hireDate;
    private EmploymentStatus employmentStatus;
    private EmploymentType employmentType;

    // Estado de cuenta
    private boolean active;
    private String activationToken;
    private LocalDateTime activationSentAt;
    private LocalDateTime activatedAt;

    // Auditoría
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final UserId createdBy;

    // ── Factory method principal ──────────────────────────────────────────

    public static Teacher create(
            TeacherId teacherId,
            UserId userId,
            FullName fullName,
            Dni dni,
            Cuil cuil,
            Email email,
            LocalDate birthDate,
            PlaceId birthPlaceId,
            Gender gender,
            Nationality nationality,
            PhoneNumber phone,
            Address address,
            TeacherSpecialization specialization,
            String teachingLicense,
            LocalDate hireDate,
            EmploymentType employmentType,
            UserId createdBy) {

        if (hireDate == null) {
            throw new IllegalArgumentException("Hire date is required");
        }
        if (employmentType == null) {
            throw new IllegalArgumentException("Employment type is required");
        }

        return Teacher.builder()
                .teacherId(teacherId)
                .userId(userId)
                .fullName(fullName)
                .dni(dni)
                .cuil(cuil)
                .email(email)
                .birthDate(birthDate)
                .birthPlaceId(birthPlaceId)
                .gender(gender)
                .nationality(nationality)
                .phone(phone)
                .address(address)
                .specialization(specialization)
                .teachingLicense(teachingLicense)
                .hireDate(hireDate)
                .employmentStatus(EmploymentStatus.ACTIVE)
                .employmentType(employmentType)
                .active(false) // requiere activación de cuenta
                .createdBy(createdBy)
                .build();
    }

    // ── Métodos de negocio ────────────────────────────────────────────────

    public void activate(LocalDateTime activatedAt) {
        this.active = true;
        this.activatedAt = activatedAt;
        this.activationToken = null; // token consumido
    }

    public void deactivate() {
        this.active = false;
    }

    public void retire() {
        this.employmentStatus = EmploymentStatus.RETIRED;
        this.active = false;
    }

    public void updateContactInfo(PhoneNumber phone, Address address) {
        this.phone = phone;
        this.address = address;
    }

    public void updateProfessionalInfo(
            TeacherSpecialization specialization,
            String teachingLicense,
            EmploymentType employmentType) {
        this.specialization = specialization;
        this.teachingLicense = teachingLicense;
        if (employmentType != null) {
            this.employmentType = employmentType;
        }
    }

    public void updatePersonalInfo(
            FullName fullName,
            LocalDate birthDate,
            PlaceId birthPlaceId,
            Gender gender,
            Nationality nationality) {
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.birthPlaceId = birthPlaceId;
        this.gender = gender;
        this.nationality = nationality;
    }

    public boolean isPendingActivation() {
        return !active && activationToken != null;
    }

    public boolean isRetired() {
        return employmentStatus == EmploymentStatus.RETIRED;
    }

    public void assignActivationToken(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Activation token cannot be blank");
        }
        this.activationToken = token;
        this.activationSentAt = java.time.LocalDateTime.now();
    }
}
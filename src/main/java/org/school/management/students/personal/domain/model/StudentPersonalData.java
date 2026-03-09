package org.school.management.students.personal.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.geography.domain.valueobject.PlaceId;
import org.school.management.shared.person.domain.valueobject.*;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Objects;

@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class StudentPersonalData {

    @EqualsAndHashCode.Include
    private final StudentPersonalDataId studentId;
    private final UserId userId;

    // Shared Kernel — identidad civil
    private final Dni dni;
    private final Cuil cuil;
    private final FullName fullName;
    private final LocalDate birthDate;
    private final Gender gender;
    private final Nationality nationality;

    // Geografía — PlaceId del Shared Kernel geography
    private final PlaceId birthPlaceId;
    private final PlaceId residencePlaceId;

    // Contacto (mutables — pueden actualizarse)
    private PhoneNumber phone;
    private Email email;
    private Address address;

    // Auditoría
    @Builder.Default
    private final LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    private final UserId createdBy;

    // ============ Factory method con validaciones de dominio ============

    public static StudentPersonalData create(StudentPersonalDataBuilder builder) {
        Objects.requireNonNull(builder.studentId, "StudentId cannot be null");
        Objects.requireNonNull(builder.userId, "UserId cannot be null");
        Objects.requireNonNull(builder.dni, "DNI cannot be null");
        Objects.requireNonNull(builder.cuil, "CUIL cannot be null");
        Objects.requireNonNull(builder.fullName, "FullName cannot be null");
        Objects.requireNonNull(builder.birthDate, "BirthDate cannot be null");
        Objects.requireNonNull(builder.birthPlaceId, "BirthPlaceId cannot be null");
        Objects.requireNonNull(builder.residencePlaceId, "ResidencePlaceId cannot be null");
        Objects.requireNonNull(builder.gender, "Gender cannot be null");
        Objects.requireNonNull(builder.nationality, "Nationality cannot be null");
        Objects.requireNonNull(builder.address, "Address cannot be null");
        Objects.requireNonNull(builder.createdBy, "CreatedBy cannot be null");

        // Validar que birthDate no sea en el futuro
        if (builder.birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("BirthDate cannot be in the future");
        }

        // Validar que CUIL corresponda al DNI
        Dni extractedDni = builder.cuil.extractDni();
        if (!extractedDni.equals(builder.dni)) {
            throw new IllegalArgumentException(
                    "CUIL does not match DNI: CUIL=" + builder.cuil.formatted()
                            + ", DNI=" + builder.dni.value()
            );
        }

        builder.updatedAt = LocalDateTime.now();
        return builder.build();
    }

    // ============ Domain Logic ============

    public int calculateAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public boolean isAdult() {
        return calculateAge() >= 18;
    }

    public boolean isEligibleForSecondarySchool() {
        int age = calculateAge();
        return age >= 11 && age <= 21;
    }

    // ============ Mutación controlada via métodos de negocio ============

    public void updateContactInfo(PhoneNumber phone, Email email) {
        this.phone = phone;
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateAddress(Address address) {
        Objects.requireNonNull(address, "Address cannot be null");
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePersonalData(FullName fullName, PhoneNumber phone,
                                   Email email, Address address) {
        // fullName y address son obligatorios; phone y email opcionales
        Objects.requireNonNull(fullName, "FullName cannot be null");
        Objects.requireNonNull(address, "Address cannot be null");
        // fullName es final — no se puede cambiar post-creación por diseño legal
        // (el nombre civil no cambia; si cambia, es un proceso formal aparte)
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }
}
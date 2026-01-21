package org.school.management.students.personal.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.geography.domain.valueobject.PlaceId;
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

    // Shared kernel – persona
    private final Dni dni;
    private final Cuil cuil;
    private final FullName fullName;
    private final LocalDate birthDate;
    private final Gender gender;
    private final Nationality nationality;

    // Geografía
    private final PlaceId birthPlaceId;
    private final PlaceId residencePlaceId; // domicilio actual

    // Contacto y documentación
    private final PhoneNumber phone;
    private final Email email;
    private final Address address;

    // Auditoría
    @Builder.Default
    private final LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private final LocalDateTime updatedAt = LocalDateTime.now();
    private final UserId createdBy;

    // ============ Domain Logic ============

    /**
     * Calcula la edad actual del estudiante
     */
    public int calculateAge() {
        assert birthDate != null;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Valida si el estudiante es mayor de edad (18 años en Argentina)
     */
    public boolean isAdult() {
        return calculateAge() >= 18;
    }

    /**
     * Valida si el estudiante puede inscribirse en nivel secundario
     * (edad mínima 12 años, máxima 21 años aprox)
     */
    public boolean isEligibleForSecondarySchool() {
        int age = calculateAge();
        return age >= 11 && age <= 21;
    }

    /**
     * Actualiza datos personales (mutación controlada)
     */
    public void updatePersonalData(
            FullName fullName,
            PhoneNumber phone,
            Email email,
            Address address,
            UserId updatedBy) {

        Objects.requireNonNull(fullName, "FullName cannot be null");
        Objects.requireNonNull(address, "Address cannot be null");
        Objects.requireNonNull(updatedBy, "UpdatedBy cannot be null");


    }

    // Validaciones de dominio en el constructor
    public static StudentPersonalData create(StudentPersonalDataBuilder builder) {
        Objects.requireNonNull(builder.studentId, "StudentId cannot be null");
        Objects.requireNonNull(builder.userId, "UserId cannot be null");
        Objects.requireNonNull(builder.dni, "DNI cannot be null");
        Objects.requireNonNull(builder.cuil, "CUIL cannot be null");
        Objects.requireNonNull(builder.fullName, "FullName cannot be null");
        Objects.requireNonNull(builder.birthDate, "BirthDate cannot be null");
        Objects.requireNonNull(builder.birthPlaceId, "BirthPlaceId cannot be null");
        Objects.requireNonNull(builder.gender, "Gender cannot be null");
        Objects.requireNonNull(builder.nationality, "Nationality cannot be null");
        Objects.requireNonNull(builder.address, "Address cannot be null");
        Objects.requireNonNull(builder.createdBy, "CreatedBy cannot be null");

        // Validar que CUIL corresponda al DNI
        Dni extractedDni = builder.cuil.extractDni();
        if (!extractedDni.equals(builder.dni)) {
            throw new IllegalArgumentException(
                    "CUIL does not match DNI: CUIL=" + builder.cuil + ", DNI=" + builder.dni
            );
        }

        // Validar edad mínima (11 años para secundaria)
        LocalDate minBirthDate = LocalDate.now().minusYears(21);
        LocalDate maxBirthDate = LocalDate.now().minusYears(11);
        if (builder.birthDate.isBefore(minBirthDate) || builder.birthDate.isAfter(maxBirthDate)) {
            throw new IllegalArgumentException(
                    "Student age must be between 12 and 21 years"
            );
        }

        return builder.build();
    }
}
package org.school.management.students.application.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.school.management.shared.person.domain.valueobject.Email;
import org.school.management.students.domain.valueobject.Relationship;

import java.time.LocalDate;
import java.util.UUID;

public record CreateStudentRequest(
        // Datos del usuario
        @NotBlank String dni,

        // Datos personales
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull @Past LocalDate birthDate,
        @NotNull UUID birthPlaceId,
        @NotNull Gender gender,

        // Contacto
        @NotNull UUID addressPlaceId,
        @NotBlank String addressStreet,
        @NotBlank String addressNumber,
        String addressFloor,
        String addressApartment,
        String postalCode,

        // Salud
        String bloodType,
        String healthInsurance,
        String healthInsuranceNumber,
        String allergies,
        String medicalObservations,

        // Inscripción
        @NotNull UUID gradeLevelId,
        boolean isRepeating,
        String previousSchool,

        // Padre/Tutor
        @NotNull ParentInfo primaryParent
) {
    public record ParentInfo(
            String dni,  // Si ya existe
            String firstName,
            String lastName,
            Email email,
            @NotBlank String phone,
            String phoneAlt,
            Relationship relationship,
            String occupation,
            String workplace
    ) {}
}
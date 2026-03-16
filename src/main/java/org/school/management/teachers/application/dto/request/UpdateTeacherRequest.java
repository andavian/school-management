package org.school.management.teachers.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.school.management.teachers.domain.valueobject.EmploymentStatus;
import org.school.management.teachers.domain.valueobject.EmploymentType;

import java.time.LocalDate;

/**
 * PATCH semántico — todos los campos son opcionales.
 * null = conservar valor existente (igual que en students/health).
 * Soporta tres secciones independientes: personal, contact, professional.
 */
public record UpdateTeacherRequest(

        // Sección personal
        @Size(max = 100) String firstName,
        @Size(max = 100) String lastName,

        LocalDate birthDate,
        String birthPlaceId,

        @Pattern(regexp = "MALE|FEMALE|OTHER", message = "Gender must be MALE, FEMALE or OTHER")
        String gender,

        @Size(max = 100) String nationality,

        // Sección contacto
        @Size(max = 20)  String phone,

        @Email @Size(max = 254) String email,

        CreateTeacherRequest.AddressRequest address,

        // Sección profesional
        @Size(max = 200) String specialization,
        @Size(max = 100) String teachingLicense,
        EmploymentType employmentType,
        EmploymentStatus employmentStatus
) {}
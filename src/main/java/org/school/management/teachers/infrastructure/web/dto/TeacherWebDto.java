package org.school.management.teachers.infrastructure.web.dto;

import jakarta.validation.constraints.*;
import org.school.management.teachers.domain.valueobject.EmploymentStatus;
import org.school.management.teachers.domain.valueobject.EmploymentType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public final class TeacherWebDto {

    private TeacherWebDto() {}

    // ── Requests ──────────────────────────────────────────────────────────

    public record CreateTeacherWebRequest(

            @NotBlank(message = "First name is required")
            @Size(max = 100) String firstName,

            @NotBlank(message = "Last name is required")
            @Size(max = 100) String lastName,

            @NotBlank(message = "DNI is required")
            @Pattern(regexp = "\\d{8}", message = "DNI must be exactly 8 digits")
            String dni,

            @NotBlank(message = "CUIL is required")
            @Pattern(regexp = "\\d{11}", message = "CUIL must be exactly 11 digits")
            String cuil,

            @NotBlank(message = "Email is required")
            @Email(message = "Email format is invalid")
            @Size(max = 254) String email,

            LocalDate birthDate,
            String birthPlaceId,

            @Pattern(regexp = "MALE|FEMALE|OTHER",
                    message = "Gender must be MALE, FEMALE or OTHER")
            String gender,

            @Size(max = 100) String nationality,

            @NotBlank(message = "Phone is required")
            @Size(max = 20) String phone,

            AddressWebRequest address,

            @Size(max = 200) String specialization,
            @Size(max = 100) String teachingLicense,

            @NotNull(message = "Hire date is required")
            LocalDate hireDate,

            @NotNull(message = "Employment type is required")
            EmploymentType employmentType
    ) {}

    public record UpdateTeacherWebRequest(

            @Size(max = 100) String firstName,
            @Size(max = 100) String lastName,

            LocalDate birthDate,
            String birthPlaceId,

            @Pattern(regexp = "MALE|FEMALE|OTHER",
                    message = "Gender must be MALE, FEMALE or OTHER")
            String gender,

            @Size(max = 100) String nationality,

            @Size(max = 20)  String phone,

            @Email @Size(max = 254) String email,

            AddressWebRequest address,

            @Size(max = 200) String specialization,
            @Size(max = 100) String teachingLicense,
            EmploymentType employmentType,
            EmploymentStatus employmentStatus
    ) {}

    public record AddressWebRequest(
            @Size(max = 200) String street,
            @Size(max = 10)  String number,
            @Size(max = 10)  String floor,
            @Size(max = 10)  String apartment,
            String placeId,
            @Size(max = 10)  String postalCode
    ) {}

    // ── Responses ─────────────────────────────────────────────────────────

    public record TeacherWebResponse(
            UUID teacherId,
            UUID userId,
            String firstName,
            String lastName,
            String fullName,
            String dni,
            String cuil,
            String email,
            LocalDate birthDate,
            String gender,
            String nationality,
            String phone,
            AddressWebResponse address,
            PlaceWebResponse birthPlace,
            PlaceWebResponse residencePlace,
            String specialization,
            String teachingLicense,
            LocalDate hireDate,
            EmploymentStatus employmentStatus,
            EmploymentType employmentType,
            boolean active,
            boolean pendingActivation,
            LocalDateTime activatedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    public record TeacherSummaryWebResponse(
            UUID teacherId,
            String firstName,
            String lastName,
            String fullName,
            String dni,
            String email,
            String phone,
            String specialization,
            EmploymentStatus employmentStatus,
            EmploymentType employmentType,
            boolean active
    ) {}

    public record TeacherSearchWebResponse(
            java.util.List<TeacherSummaryWebResponse> teachers,
            int total
    ) {}

    public record AddressWebResponse(
            String street,
            String number,
            String floor,
            String apartment,
            String postalCode,
            UUID placeId
    ) {}

    public record PlaceWebResponse(
            UUID placeId,
            String placeName,
            String provinceName,
            String countryName
    ) {}
}
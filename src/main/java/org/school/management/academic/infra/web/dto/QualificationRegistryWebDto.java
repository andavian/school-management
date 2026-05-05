package org.school.management.academic.infra.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public final class QualificationRegistryWebDto {

    private QualificationRegistryWebDto() {}

    // ========================================================================
    // WEB REQUESTS
    // ========================================================================

    public record InitRegistrySequenceWebRequest(
            @NotBlank(message = "Academic year ID is required")
            String academicYearId,

            @NotNull(message = "Last paper number is required")
            @Min(value = 0, message = "Last paper number must be >= 0")
            @Max(value = 998, message = "Last paper number must be <= 998")
            Integer lastPaperNumber
    ) {}

    public record CreateQualificationRegistryWebRequest(
            @NotBlank(message = "Academic year ID is required")
            String academicYearId
            // startFolio, endFolio y maxFolios van fijos por el factory
    ) {}

    public record CloseRegistryWebRequest(
            @NotBlank(message = "Registry ID is required")
            String registryId
    ) {}

    public record ReactivateRegistryWebRequest(
            @NotBlank(message = "Registry ID is required")
            String registryId
    ) {}

    // ========================================================================
    // WEB RESPONSE
    // ========================================================================

    public record QualificationRegistryWebResponse(
            String registryId,
            String registryNumber,
            String academicYearId,
            Integer year,   // año del año académico (se llena luego si es necesario)
            Integer startFolio,
            Integer endFolio,
            Integer currentFolio,
            Integer maxFolios,
            String status,
            Integer availableFolios,
            Integer usedFolios,
            Double usagePercentage,
            boolean isNearlyFull,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
package org.school.management.academic.application.dto.response;

import java.time.LocalDateTime;


public record QualificationRegistryResponse (
        String registryId,
        String registryNumber,
        String academicYearId,
        Integer year,  // From AcademicYea,
        Integer startFolio,
        Integer endFolio,
        Integer currentFolio,
        Integer maxFolios,
        String status,
        Integer availableFolios,  // Compute,
        Integer usedFolios,  // Compute,
        Double usagePercentage,  // Compute,
        boolean isNearlyFull,  // Computed: < 50 folios availabl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){

}

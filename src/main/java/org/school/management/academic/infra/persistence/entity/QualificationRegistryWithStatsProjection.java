package org.school.management.academic.infra.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QualificationRegistryWithStatsProjection {
    // QualificationRegistry fields
    private UUID qualificationRegistryId;
    private String registryNumber;
    private Integer startFolio;
    private Integer endFolio;
    private Integer currentFolio;
    private Integer maxFolios;
    private String status;

    // AcademicYear fields
    private UUID academicYearId;
    private Integer year;
    private Boolean isCurrentYear;

    // Calculated stats
    private Integer usedFolios;
    private Integer availableFolios;
    private Double usagePercentage;

    public Integer getAvailableFolios() {
        if (availableFolios != null) {
            return availableFolios;
        }
        return endFolio - currentFolio + 1;
    }

    public Integer getUsedFolios() {
        if (usedFolios != null) {
            return usedFolios;
        }
        return currentFolio - startFolio;
    }

    public Double getUsagePercentage() {
        if (usagePercentage != null) {
            return usagePercentage;
        }
        if (maxFolios == 0) {
            return 0.0;
        }
        return (getUsedFolios() * 100.0) / maxFolios;
    }

    public boolean isNearlyFull() {
        return getAvailableFolios() < 50;
    }

    public boolean isFull() {
        return currentFolio >= endFolio;
    }

    public boolean canAssignFolios() {
        return "ACTIVE".equals(status) && !isFull();
    }
}

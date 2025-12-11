package org.school.management.academic.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.school.management.academic.domain.exception.RegistryFullException;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.RegistryId;
import org.school.management.academic.domain.valueobject.RegistryNumber;
import org.school.management.academic.domain.valueobject.enums.RegistryStatus;

import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
public class QualificationRegistry {
    RegistryId registryId;
    RegistryNumber registryNumber;
    AcademicYearId academicYearId;
    int startFolio;
    int endFolio;
    @With
    int currentFolio;
    int maxFolios;
    @With
    RegistryStatus status;
    LocalDateTime createdAt;
    LocalDateTime closedAt;
    LocalDateTime updatedAt;

    public static QualificationRegistry create(
            String registryNumber,
            AcademicYearId academicYearId,
            int maxFolios
    ) {
        if (maxFolios <= 0) {
            throw new IllegalArgumentException("Max folios must be positive");
        }

        return QualificationRegistry.builder()
                .registryId(RegistryId.generate())
                .registryNumber(RegistryNumber.of(registryNumber))
                .academicYearId(academicYearId)
                .startFolio(1)
                .endFolio(maxFolios)
                .currentFolio(1)
                .maxFolios(maxFolios)
                .status(RegistryStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public boolean isFull() {
        return currentFolio > endFolio;
    }

    public boolean isActive() {
        return status == RegistryStatus.ACTIVE;
    }

    public int getAvailableFolios() {
        return Math.max(0, endFolio - currentFolio + 1);
    }

    public QualificationRegistry close() {
        return this.toBuilder()
                .status(RegistryStatus.CLOSED)
                .closedAt(LocalDateTime.now())
                .build();
    }

    public String getRegistryNumberAsString() {
        return registryNumber.getValue();
    }

     /**
     * Marca el registro como lleno
     */
    public QualificationRegistry markAsFull() {
        return this.toBuilder()
                .status(RegistryStatus.FULL)
                .updatedAt(LocalDateTime.now())
                .build();
    }


    /**
     * Verifica si hay folios disponibles
     */
    public boolean hasAvailableFolios() {
        return currentFolio <= endFolio && status == RegistryStatus.ACTIVE;
    }

        /**
     * Obtiene la cantidad de folios usados
     */
    public int getUsedFolios() {
        return currentFolio - startFolio;
    }

    /**
     * Calcula el porcentaje de uso
     */
    public double getUsagePercentage() {
        if (maxFolios == 0) return 0.0;
        return (getUsedFolios() * 100.0) / maxFolios;
    }

    /**
     * Verifica si el registro está cerca de llenarse
     */
    public boolean isNearlyFull(int threshold) {
        return getAvailableFolios() < threshold;
    }
}


package org.school.management.academic.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "qualification_registries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QualificationRegistryEntity {
    @Id
    @Column(name = "registry_id", columnDefinition = "BINARY(16)")
    private UUID registryId;

    @Column(name = "registry_number", nullable = false, unique = true, length = 20)
    private String registryNumber;

    @Column(name = "academic_year_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID academicYearId;

    @Column(name = "start_folio", nullable = false)
    private Integer startFolio;

    @Column(name = "end_folio", nullable = false)
    private Integer endFolio;

    @Column(name = "current_folio", nullable = false)
    private Integer currentFolio;

    @Column(name = "max_folios", nullable = false)
    private Integer maxFolios;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "updated_at", nullable = false, updatable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}

package org.school.management.academic.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.school.management.academic.domain.valueobject.enums.AcademicYearStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

// ============================================================================
// BASE ENTITIES (Ya existentes - Reference)
// ============================================================================

@Entity
@Table(name = "academic_years")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicYearEntity {
    @Id
    @Column(name = "academic_year_id", columnDefinition = "BINARY(16)")
    private UUID academicYearId;

    @Column(name = "year", nullable = false, unique = true)
    private Integer year;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AcademicYearStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}


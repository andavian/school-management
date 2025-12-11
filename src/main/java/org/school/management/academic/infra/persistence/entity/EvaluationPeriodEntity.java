package org.school.management.academic.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "evaluation_periods")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationPeriodEntity {
    @Id
    @Column(name = "period_id", columnDefinition = "BINARY(16)")
    private UUID periodId;

    @Column(name = "academic_year_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID academicYearId;

    @Column(name = "period_number", nullable = false)
    private Integer periodNumber;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "is_current", nullable = false)
    private Boolean isCurrent;

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

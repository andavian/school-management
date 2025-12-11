package org.school.management.academic.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "grade_levels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeLevelEntity {
    @Id
    @Column(name = "grade_level_id", columnDefinition = "BINARY(16)")
    private UUID gradeLevelId;

    @Column(name = "academic_year_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID academicYearId;

    @Column(name = "year_level", nullable = false)
    private Integer yearLevel;

    @Column(name = "division", nullable = false, length = 2)
    private String division;

    @Column(name = "orientation_id", columnDefinition = "BINARY(16)")
    private UUID orientationId;

    @Column(name = "shift", nullable = false, length = 10)
    private String shift;

    @Column(name = "max_students", nullable = false)
    private Integer maxStudents;

    @Column(name = "homeroom_teacher_id", columnDefinition = "BINARY(16)")
    private UUID homeroomTeacherId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

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

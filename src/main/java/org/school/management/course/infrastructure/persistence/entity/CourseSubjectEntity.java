package org.school.management.course.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.course.domain.valueobject.CourseStatus;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "course_subjects")
public class CourseSubjectEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "course_subject_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID courseSubjectId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "grade_level_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID gradeLevelId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "subject_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID subjectId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "teacher_id", columnDefinition = "BINARY(16)")
    private UUID teacherId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "academic_year_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID academicYearId;

    @Column(name = "schedule_json", columnDefinition = "JSON")
    private String scheduleJson;

    @Column(name = "classroom", length = 50)
    private String classroom;

    @Column(name = "min_passing_grade", nullable = false, precision = 4, scale = 2)
    private BigDecimal minPassingGrade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CourseStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onPrePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    protected void onPreUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
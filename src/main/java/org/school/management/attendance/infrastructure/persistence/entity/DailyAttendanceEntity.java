// org.school.management.attendance.infrastructure.persistence.entity.DailyAttendanceEntity
package org.school.management.attendance.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "attendance_daily_records",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_daily_student_date",
                columnNames = {"student_id", "attendance_date"}))
public class DailyAttendanceEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "daily_attendance_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID dailyAttendanceId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "student_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID studentId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "grade_level_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID gradeLevelId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "academic_year_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID academicYearId;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "justification_reason", length = 500)
    private String justificationReason;

    @Column(name = "observations", length = 500)
    private String observations;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "recorded_by_user_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID recordedByUserId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "corrected_by_user_id", columnDefinition = "BINARY(16)")
    private UUID correctedByUserId;

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
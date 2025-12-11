package org.school.management.students.domain.model;


import lombok.Builder;
import lombok.Value;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.parents.domain.valueobject.StudentId;
import org.school.management.students.enrollment.domain.valueobject.EnrollmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
@Builder
public class Enrollment {
    EnrollmentId enrollmentId;
    StudentId studentId;
    GradeLevelId gradeLevelId;
    AcademicYearId academicYearId;

    LocalDate enrollmentDate;
    EnrollmentType enrollmentType;
    EnrollmentStatus status;

    boolean isRepeating;
    String previousSchool;
    LocalDate transferDate;

    BigDecimal finalAverage;
    Boolean passed;
    LocalDate completionDate;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    UserId createdBy;

    public static Enrollment createNew(
            StudentId studentId,
            GradeLevelId gradeLevelId,
            AcademicYearId academicYearId,
            EnrollmentType type,
            boolean isRepeating,
            UserId createdBy
    ) {
        return Enrollment.builder()
                .enrollmentId(EnrollmentId.generate())
                .studentId(studentId)
                .gradeLevelId(gradeLevelId)
                .academicYearId(academicYearId)
                .enrollmentDate(LocalDate.now())
                .enrollmentType(type)
                .status(EnrollmentStatus.ACTIVE)
                .isRepeating(isRepeating)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();
    }

    public Enrollment complete(BigDecimal finalAverage, boolean passed) {
        return this.toBuilder()
                .status(EnrollmentStatus.GRADUATED)
                .finalAverage(finalAverage)
                .passed(passed)
                .completionDate(LocalDate.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

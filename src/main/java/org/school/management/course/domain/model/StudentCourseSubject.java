package org.school.management.course.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.school.management.academic.domain.valueobject.enums.SubjectEnrollmentStatus;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class StudentCourseSubject {
    StudentCourseSubjectId id;
    UUID enrollmentId;
    UUID courseSubjectId;

    @With
    SubjectEnrollmentStatus status;

    int totalClasses;
    int attendedClasses;
    @With
    BigDecimal attendancePercentage;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public static StudentCourseSubject enroll(
            UUID enrollmentId,
            UUID courseSubjectId
    ) {
        return StudentCourseSubject.builder()
                .id(StudentCourseSubjectId.generate())
                .enrollmentId(enrollmentId)
                .courseSubjectId(courseSubjectId)
                .status(SubjectEnrollmentStatus.ENROLLED)
                .totalClasses(0)
                .attendedClasses(0)
                .attendancePercentage(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public StudentCourseSubject recordAttendance(boolean attended) {
        int newTotal = totalClasses + 1;
        int newAttended = attended ? attendedClasses + 1 : attendedClasses;
        BigDecimal newPercentage = BigDecimal.valueOf(newAttended)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(newTotal), 2, RoundingMode.HALF_UP);

        return this.toBuilder()
                .totalClasses(newTotal)
                .attendedClasses(newAttended)
                .attendancePercentage(newPercentage)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public StudentCourseSubject markAsFree() {
        if (attendancePercentage.compareTo(BigDecimal.valueOf(75)) < 0) {
            return this.withStatus(SubjectEnrollmentStatus.FREE);
        }
        throw new IllegalStateException("Cannot mark as free: attendance >= 75%");
    }

    public StudentCourseSubject pass() {
        return this.withStatus(SubjectEnrollmentStatus.PASSED);
    }

    public StudentCourseSubject fail() {
        return this.withStatus(SubjectEnrollmentStatus.FAILED);
    }

    public boolean meetsAttendanceRequirement() {
        return attendancePercentage.compareTo(BigDecimal.valueOf(75)) >= 0;
    }
}

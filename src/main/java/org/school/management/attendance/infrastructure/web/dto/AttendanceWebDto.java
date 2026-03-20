// org.school.management.attendance.infrastructure.web.dto.AttendanceWebDto
package org.school.management.attendance.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public final class AttendanceWebDto {

    private AttendanceWebDto() {}

    // ── Requests ──

    public record RecordDailyAttendanceWebRequest(
            @NotNull UUID studentId,
            @NotNull UUID gradeLevelId,
            @NotNull UUID academicYearId,
            @NotNull @PastOrPresent LocalDate attendanceDate,
            @NotBlank String status,
            String observations
    ) {}

    public record RecordCourseAttendanceWebRequest(
            @NotNull UUID studentCourseSubjectId,
            @NotNull UUID courseSubjectId,
            @NotNull UUID periodId,
            @NotNull @PastOrPresent LocalDate classDate,
            @NotBlank String status,
            String observations
    ) {}

    public record JustifyAbsenceWebRequest(
            @NotBlank @Size(max = 500) String reason
    ) {}

    public record CorrectAttendanceWebRequest(
            @NotBlank String newStatus,
            String observations
    ) {}

    // ── Responses ──

    public record DailyAttendanceWebResponse(
            UUID dailyAttendanceId,
            UUID studentId,
            UUID gradeLevelId,
            UUID academicYearId,
            LocalDate attendanceDate,
            String status,
            String justificationReason,
            String observations,
            UUID recordedByUserId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    public record CourseAttendanceWebResponse(
            UUID courseAttendanceId,
            UUID studentCourseSubjectId,
            UUID courseSubjectId,
            UUID periodId,
            LocalDate classDate,
            String status,
            double absenceWeight,
            String observations,
            UUID recordedByUserId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    public record AttendanceSummaryWebResponse(
            UUID attendanceSummaryId,
            UUID studentCourseSubjectId,
            UUID courseSubjectId,
            UUID periodId,
            int totalClasses,
            int presentCount,
            int absentCount,
            int justifiedCount,
            int lateCount,
            int withdrawnCount,
            double weightedAbsences,
            double attendancePercentage,
            boolean atRisk
    ) {}

    public record AtRiskStudentsWebResponse(
            List<AttendanceSummaryWebResponse> atRiskStudents,
            int total
    ) {}
}
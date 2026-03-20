// org.school.management.attendance.application.dto.response.AttendanceSummaryResponse
package org.school.management.attendance.application.dto.response;

import java.util.UUID;

public record AttendanceSummaryResponse(
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
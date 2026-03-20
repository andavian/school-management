// org.school.management.attendance.infrastructure.web.mapper.AttendanceWebMapper
package org.school.management.attendance.infrastructure.web.mapper;

import org.mapstruct.Mapper;
import org.school.management.attendance.application.dto.request.CorrectAttendanceRequest;
import org.school.management.attendance.application.dto.request.JustifyAbsenceRequest;
import org.school.management.attendance.application.dto.request.RecordCourseAttendanceRequest;
import org.school.management.attendance.application.dto.request.RecordDailyAttendanceRequest;
import org.school.management.attendance.application.dto.response.AttendanceSummaryResponse;
import org.school.management.attendance.application.dto.response.CourseAttendanceResponse;
import org.school.management.attendance.application.dto.response.DailyAttendanceResponse;
import org.school.management.attendance.infrastructure.web.dto.AttendanceWebDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AttendanceWebMapper {

    RecordDailyAttendanceRequest toAppRequest(AttendanceWebDto.RecordDailyAttendanceWebRequest web);

    RecordCourseAttendanceRequest toAppRequest(AttendanceWebDto.RecordCourseAttendanceWebRequest web);

    JustifyAbsenceRequest toAppRequest(AttendanceWebDto.JustifyAbsenceWebRequest web);

    CorrectAttendanceRequest toAppRequest(AttendanceWebDto.CorrectAttendanceWebRequest web);

    AttendanceWebDto.DailyAttendanceWebResponse toWebResponse(DailyAttendanceResponse app);

    AttendanceWebDto.CourseAttendanceWebResponse toWebResponse(CourseAttendanceResponse app);

    AttendanceWebDto.AttendanceSummaryWebResponse toWebResponse(AttendanceSummaryResponse app);

    default AttendanceWebDto.AtRiskStudentsWebResponse toAtRiskResponse(
            List<AttendanceSummaryResponse> atRiskList) {
        List<AttendanceWebDto.AttendanceSummaryWebResponse> webList = atRiskList.stream()
                .map(this::toWebResponse)
                .toList();
        return new AttendanceWebDto.AtRiskStudentsWebResponse(webList, webList.size());
    }
}
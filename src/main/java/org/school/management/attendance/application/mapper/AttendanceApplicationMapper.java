// org.school.management.attendance.application.mapper.AttendanceApplicationMapper
package org.school.management.attendance.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.school.management.attendance.domain.model.AttendanceSummary;
import org.school.management.attendance.domain.model.CourseAttendance;
import org.school.management.attendance.domain.model.DailyAttendance;
import org.school.management.attendance.application.dto.response.AttendanceSummaryResponse;
import org.school.management.attendance.application.dto.response.CourseAttendanceResponse;
import org.school.management.attendance.application.dto.response.DailyAttendanceResponse;

@Mapper(componentModel = "spring")
public interface AttendanceApplicationMapper {

    @Mapping(target = "dailyAttendanceId",
            expression = "java(domain.getDailyAttendanceId().value())")
    @Mapping(target = "studentId",
            expression = "java(domain.getStudentId().value())")
    @Mapping(target = "gradeLevelId",
            expression = "java(domain.getGradeLevelId().value())")
    @Mapping(target = "academicYearId",
            expression = "java(domain.getAcademicYearId().value())")
    @Mapping(target = "status",
            expression = "java(domain.getStatus().name())")
    DailyAttendanceResponse toDailyAttendanceResponse(DailyAttendance domain);

    @Mapping(target = "courseAttendanceId",
            expression = "java(domain.getCourseAttendanceId().value())")
    @Mapping(target = "studentCourseSubjectId",
            expression = "java(domain.getStudentCourseSubjectId().value())")
    @Mapping(target = "courseSubjectId",
            expression = "java(domain.getCourseSubjectId().value())")
    @Mapping(target = "periodId",
            expression = "java(domain.getPeriodId().value())")
    @Mapping(target = "status",
            expression = "java(domain.getStatus().name())")
    @Mapping(target = "absenceWeight",
            expression = "java(domain.getAbsenceWeight())")
    CourseAttendanceResponse toCourseAttendanceResponse(CourseAttendance domain);

    @Mapping(target = "attendanceSummaryId",
            expression = "java(domain.getAttendanceSummaryId().value())")
    @Mapping(target = "studentCourseSubjectId",
            expression = "java(domain.getStudentCourseSubjectId().value())")
    @Mapping(target = "courseSubjectId",
            expression = "java(domain.getCourseSubjectId().value())")
    @Mapping(target = "periodId",
            expression = "java(domain.getPeriodId().value())")
    AttendanceSummaryResponse toAttendanceSummaryResponse(AttendanceSummary domain);
}
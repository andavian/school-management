// org.school.management.attendance.application.usecases.RecordCourseAttendanceUseCase
package org.school.management.attendance.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.attendance.domain.exception.AttendanceAlreadyRecordedException;
import org.school.management.attendance.domain.model.AttendanceSummary;
import org.school.management.attendance.domain.model.CourseAttendance;
import org.school.management.attendance.domain.repository.AttendanceSummaryRepository;
import org.school.management.attendance.domain.repository.CourseAttendanceRepository;
import org.school.management.attendance.domain.valueobject.AttendanceStatus;
import org.school.management.attendance.domain.valueobject.AttendanceSummaryId;
import org.school.management.attendance.domain.valueobject.CourseAttendanceId;
import org.school.management.attendance.application.dto.request.RecordCourseAttendanceRequest;
import org.school.management.attendance.application.dto.response.CourseAttendanceResponse;
import org.school.management.attendance.application.mapper.AttendanceApplicationMapper;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RecordCourseAttendanceUseCase {

    private final CourseAttendanceRepository courseAttendanceRepository;
    private final AttendanceSummaryRepository attendanceSummaryRepository;
    private final AttendanceApplicationMapper mapper;

    public CourseAttendanceResponse execute(RecordCourseAttendanceRequest request,
                                            UUID recordedByUserId) {
        StudentCourseSubjectId studentCourseSubjectId =
                StudentCourseSubjectId.of(request.studentCourseSubjectId());
        CourseSubjectId courseSubjectId = CourseSubjectId.of(request.courseSubjectId());
        PeriodId periodId = PeriodId.of(request.periodId());
        AttendanceStatus status = AttendanceStatus.valueOf(request.status());

        if (courseAttendanceRepository.existsByStudentCourseSubjectIdAndClassDate(
                studentCourseSubjectId, request.classDate())) {
            throw AttendanceAlreadyRecordedException.forCourseAttendance(
                    request.studentCourseSubjectId(), request.classDate());
        }

        CourseAttendance attendance = CourseAttendance.create(
                CourseAttendanceId.generate(),
                studentCourseSubjectId,
                courseSubjectId,
                periodId,
                request.classDate(),
                status,
                request.observations(),
                recordedByUserId
        );

        CourseAttendance saved = courseAttendanceRepository.save(attendance);
        log.info("CourseAttendance recorded for studentCourseSubjectId={} date={} status={}",
                request.studentCourseSubjectId(), request.classDate(), status);

        // Recalcular resumen del período
        updateAttendanceSummary(studentCourseSubjectId, courseSubjectId, periodId);

        return mapper.toCourseAttendanceResponse(saved);
    }

    private void updateAttendanceSummary(StudentCourseSubjectId studentCourseSubjectId,
                                         CourseSubjectId courseSubjectId,
                                         PeriodId periodId) {
        List<CourseAttendance> allRecords = courseAttendanceRepository
                .findAllByStudentCourseSubjectIdAndPeriodId(studentCourseSubjectId, periodId);

        Optional<AttendanceSummary> existing = attendanceSummaryRepository
                .findByStudentCourseSubjectIdAndPeriodId(studentCourseSubjectId, periodId);

        AttendanceSummary summary = existing.orElseGet(() ->
                AttendanceSummary.create(
                        AttendanceSummaryId.generate(),
                        studentCourseSubjectId,
                        courseSubjectId,
                        periodId));

        summary.recalculate(allRecords);
        attendanceSummaryRepository.save(summary);
        log.debug("AttendanceSummary updated — studentCourseSubjectId={} atRisk={}",
                studentCourseSubjectId.value(), summary.isAtRisk());
    }
}
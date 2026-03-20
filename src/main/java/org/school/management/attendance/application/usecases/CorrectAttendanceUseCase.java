// org.school.management.attendance.application.usecases.CorrectAttendanceUseCase
package org.school.management.attendance.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.attendance.domain.exception.AttendanceNotFoundException;
import org.school.management.attendance.domain.model.AttendanceSummary;
import org.school.management.attendance.domain.model.CourseAttendance;
import org.school.management.attendance.domain.model.DailyAttendance;
import org.school.management.attendance.domain.repository.AttendanceSummaryRepository;
import org.school.management.attendance.domain.repository.CourseAttendanceRepository;
import org.school.management.attendance.domain.repository.DailyAttendanceRepository;
import org.school.management.attendance.domain.valueobject.AttendanceStatus;
import org.school.management.attendance.domain.valueobject.AttendanceSummaryId;
import org.school.management.attendance.domain.valueobject.CourseAttendanceId;
import org.school.management.attendance.domain.valueobject.DailyAttendanceId;
import org.school.management.attendance.application.dto.request.CorrectAttendanceRequest;
import org.school.management.attendance.application.dto.response.CourseAttendanceResponse;
import org.school.management.attendance.application.dto.response.DailyAttendanceResponse;
import org.school.management.attendance.application.mapper.AttendanceApplicationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CorrectAttendanceUseCase {

    private final DailyAttendanceRepository dailyAttendanceRepository;
    private final CourseAttendanceRepository courseAttendanceRepository;
    private final AttendanceSummaryRepository attendanceSummaryRepository;
    private final AttendanceApplicationMapper mapper;

    /**
     * Corrige un registro de asistencia diaria (preceptor/STAFF).
     */
    public DailyAttendanceResponse correctDaily(UUID dailyAttendanceId,
                                                CorrectAttendanceRequest request,
                                                UUID correctedByUserId) {
        DailyAttendance attendance = dailyAttendanceRepository
                .findById(DailyAttendanceId.from(dailyAttendanceId))
                .orElseThrow(() -> AttendanceNotFoundException.dailyById(dailyAttendanceId));

        AttendanceStatus newStatus = AttendanceStatus.valueOf(request.newStatus());
        attendance.correct(newStatus, request.observations(), correctedByUserId);

        DailyAttendance saved = dailyAttendanceRepository.save(attendance);
        log.info("DailyAttendance id={} corrected to {} by userId={}",
                dailyAttendanceId, newStatus, correctedByUserId);
        return mapper.toDailyAttendanceResponse(saved);
    }

    /**
     * Corrige un registro de asistencia por materia (docente/TEACHER).
     * Después de corregir, recalcula el resumen del período.
     */
    public CourseAttendanceResponse correctCourse(UUID courseAttendanceId,
                                                  CorrectAttendanceRequest request,
                                                  UUID correctedByUserId) {
        CourseAttendance attendance = courseAttendanceRepository
                .findById(CourseAttendanceId.from(courseAttendanceId))
                .orElseThrow(() -> AttendanceNotFoundException.courseById(courseAttendanceId));

        AttendanceStatus newStatus = AttendanceStatus.valueOf(request.newStatus());
        attendance.correct(newStatus, request.observations(), correctedByUserId);

        CourseAttendance saved = courseAttendanceRepository.save(attendance);
        log.info("CourseAttendance id={} corrected to {} by userId={}",
                courseAttendanceId, newStatus, correctedByUserId);

        // Recalcular resumen del período
        recalculateSummary(saved);

        return mapper.toCourseAttendanceResponse(saved);
    }

    private void recalculateSummary(CourseAttendance corrected) {
        List<CourseAttendance> allRecords = courseAttendanceRepository
                .findAllByStudentCourseSubjectIdAndPeriodId(
                        corrected.getStudentCourseSubjectId(),
                        corrected.getPeriodId());

        Optional<AttendanceSummary> existing = attendanceSummaryRepository
                .findByStudentCourseSubjectIdAndPeriodId(
                        corrected.getStudentCourseSubjectId(),
                        corrected.getPeriodId());

        AttendanceSummary summary = existing.orElseGet(() ->
                AttendanceSummary.create(
                        AttendanceSummaryId.generate(),
                        corrected.getStudentCourseSubjectId(),
                        corrected.getCourseSubjectId(),
                        corrected.getPeriodId()));

        summary.recalculate(allRecords);
        attendanceSummaryRepository.save(summary);
    }
}
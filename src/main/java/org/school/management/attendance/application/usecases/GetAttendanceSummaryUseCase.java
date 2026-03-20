// org.school.management.attendance.application.usecases.GetAttendanceSummaryUseCase
package org.school.management.attendance.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.attendance.domain.exception.AttendanceNotFoundException;
import org.school.management.attendance.domain.repository.AttendanceSummaryRepository;
import org.school.management.attendance.application.dto.response.AttendanceSummaryResponse;
import org.school.management.attendance.application.mapper.AttendanceApplicationMapper;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GetAttendanceSummaryUseCase {

    private final AttendanceSummaryRepository attendanceSummaryRepository;
    private final AttendanceApplicationMapper mapper;

    public AttendanceSummaryResponse execute(UUID studentCourseSubjectId, UUID periodId) {
        return attendanceSummaryRepository
                .findByStudentCourseSubjectIdAndPeriodId(
                        StudentCourseSubjectId.of(studentCourseSubjectId),
                        PeriodId.of(periodId))
                .map(mapper::toAttendanceSummaryResponse)
                .orElseThrow(() -> AttendanceNotFoundException.summaryByStudentAndPeriod(
                        studentCourseSubjectId, periodId));
    }
}
// org.school.management.attendance.application.usecases.GetAtRiskStudentsUseCase
package org.school.management.attendance.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.attendance.domain.repository.AttendanceSummaryRepository;
import org.school.management.attendance.application.dto.response.AttendanceSummaryResponse;
import org.school.management.attendance.application.mapper.AttendanceApplicationMapper;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GetAtRiskStudentsUseCase {

    private final AttendanceSummaryRepository attendanceSummaryRepository;
    private final AttendanceApplicationMapper mapper;

    public List<AttendanceSummaryResponse> execute(UUID courseSubjectId, UUID periodId) {
        return attendanceSummaryRepository
                .findAtRiskByCourseSubjectIdAndPeriodId(
                        CourseSubjectId.of(courseSubjectId),
                        PeriodId.of(periodId))
                .stream()
                .map(mapper::toAttendanceSummaryResponse)
                .toList();
    }
}
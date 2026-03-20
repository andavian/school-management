// org.school.management.attendance.application.usecases.RecordDailyAttendanceUseCase
package org.school.management.attendance.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.attendance.domain.exception.AttendanceAlreadyRecordedException;
import org.school.management.attendance.domain.model.DailyAttendance;
import org.school.management.attendance.domain.repository.DailyAttendanceRepository;
import org.school.management.attendance.domain.valueobject.AttendanceStatus;
import org.school.management.attendance.domain.valueobject.DailyAttendanceId;
import org.school.management.attendance.application.dto.request.RecordDailyAttendanceRequest;
import org.school.management.attendance.application.dto.response.DailyAttendanceResponse;
import org.school.management.attendance.application.mapper.AttendanceApplicationMapper;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RecordDailyAttendanceUseCase {

    private final DailyAttendanceRepository dailyAttendanceRepository;
    private final AttendanceApplicationMapper mapper;

    public DailyAttendanceResponse execute(RecordDailyAttendanceRequest request,
                                           UUID recordedByUserId) {
        StudentPersonalDataId studentId = StudentPersonalDataId.of(request.studentId());
        AttendanceStatus status = AttendanceStatus.valueOf(request.status());

        if (dailyAttendanceRepository.existsByStudentIdAndDate(studentId, request.attendanceDate())) {
            throw AttendanceAlreadyRecordedException.forDailyAttendance(
                    request.studentId(), request.attendanceDate());
        }

        DailyAttendance attendance = DailyAttendance.create(
                DailyAttendanceId.generate(),
                studentId,
                GradeLevelId.of(request.gradeLevelId()),
                AcademicYearId.of(request.academicYearId()),
                request.attendanceDate(),
                status,
                request.observations(),
                recordedByUserId
        );

        DailyAttendance saved = dailyAttendanceRepository.save(attendance);
        log.info("DailyAttendance recorded for studentId={} date={} status={}",
                request.studentId(), request.attendanceDate(), status);
        return mapper.toDailyAttendanceResponse(saved);
    }
}
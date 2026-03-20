// org.school.management.attendance.application.usecases.JustifyAbsenceUseCase
package org.school.management.attendance.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.attendance.domain.exception.AttendanceNotFoundException;
import org.school.management.attendance.domain.model.DailyAttendance;
import org.school.management.attendance.domain.repository.DailyAttendanceRepository;
import org.school.management.attendance.domain.valueobject.DailyAttendanceId;
import org.school.management.attendance.application.dto.request.JustifyAbsenceRequest;
import org.school.management.attendance.application.dto.response.DailyAttendanceResponse;
import org.school.management.attendance.application.mapper.AttendanceApplicationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JustifyAbsenceUseCase {

    private final DailyAttendanceRepository dailyAttendanceRepository;
    private final AttendanceApplicationMapper mapper;

    public DailyAttendanceResponse execute(UUID dailyAttendanceId,
                                           JustifyAbsenceRequest request,
                                           UUID justifiedByUserId) {
        DailyAttendance attendance = dailyAttendanceRepository
                .findById(DailyAttendanceId.from(dailyAttendanceId))
                .orElseThrow(() -> AttendanceNotFoundException.dailyById(dailyAttendanceId));

        // justify() lanza IllegalStateException si el estado no es ABSENT
        attendance.justify(request.reason(), justifiedByUserId);

        DailyAttendance saved = dailyAttendanceRepository.save(attendance);
        log.info("DailyAttendance id={} justified by userId={}", dailyAttendanceId, justifiedByUserId);
        return mapper.toDailyAttendanceResponse(saved);
    }
}
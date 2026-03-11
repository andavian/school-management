package org.school.management.students.health.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.students.health.application.dto.response.HealthRecordResponse;
import org.school.management.students.health.application.mapper.StudentHealthRecordApplicationMapper;
import org.school.management.students.health.domain.exception.HealthRecordNotFoundException;
import org.school.management.students.health.domain.repository.StudentHealthRecordRepository;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GetHealthRecordByStudentIdUseCase {

    private final StudentHealthRecordRepository healthRecordRepository;
    private final StudentHealthRecordApplicationMapper mapper;

    public HealthRecordResponse execute(UUID studentId) {
        log.debug("Fetching health record for studentId: {}", studentId);

        var healthRecord = healthRecordRepository
                .findByStudentId(StudentPersonalDataId.of(studentId))
                .orElseThrow(() -> HealthRecordNotFoundException.byStudentId(studentId));

        return mapper.toHealthRecordResponse(healthRecord);
    }
}
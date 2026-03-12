package org.school.management.students.enrollment.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.students.enrollment.application.dto.response.EnrollmentResponse;
import org.school.management.students.enrollment.application.mapper.StudentEnrollmentApplicationMapper;
import org.school.management.students.enrollment.domain.repository.StudentEnrollmentRepository;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Retorna el historial completo de inscripciones de un estudiante.
 * Útil para ver el recorrido académico año por año.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GetEnrollmentByStudentIdUseCase {

    private final StudentEnrollmentRepository enrollmentRepository;
    private final StudentEnrollmentApplicationMapper mapper;

    public List<EnrollmentResponse> execute(UUID studentId) {
        log.debug("Fetching all enrollments for studentId: {}", studentId);

        return enrollmentRepository
                .findAllByStudentId(StudentPersonalDataId.from(studentId))
                .stream()
                .map(mapper::toEnrollmentResponse)
                .toList();
    }
}
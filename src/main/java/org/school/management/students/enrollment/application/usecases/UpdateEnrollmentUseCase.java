package org.school.management.students.enrollment.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.domain.valueobject.ids.WithdrawalReasonId;
import org.school.management.students.enrollment.application.dto.request.UpdateEnrollmentRequest;
import org.school.management.students.enrollment.application.dto.response.EnrollmentResponse;
import org.school.management.students.enrollment.application.mapper.StudentEnrollmentApplicationMapper;
import org.school.management.students.enrollment.domain.exception.EnrollmentNotFoundException;
import org.school.management.students.enrollment.domain.model.StudentEnrollment;
import org.school.management.students.enrollment.domain.repository.StudentEnrollmentRepository;
import org.school.management.students.enrollment.domain.valueobject.EnrollmentId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Actualiza una inscripción existente.
 *
 * Soporta dos operaciones mutuamente excluyentes:
 *  - Cierre de ciclo: si viene finalAverage → llama enrollment.complete()
 *  - Baja: si viene withdrawalReasonId → llama enrollment.withdraw()
 *
 * La lógica de validación de estados terminales vive en el dominio.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UpdateEnrollmentUseCase {

    private final StudentEnrollmentRepository enrollmentRepository;
    private final StudentEnrollmentApplicationMapper mapper;

    public EnrollmentResponse execute(UUID enrollmentId, UpdateEnrollmentRequest request) {
        log.debug("Updating enrollment — enrollmentId: {}", enrollmentId);

        StudentEnrollment enrollment = enrollmentRepository
                .findByEnrollmentId(EnrollmentId.from(enrollmentId))
                .orElseThrow(() -> EnrollmentNotFoundException.byId(enrollmentId));

        // Cierre de ciclo
        if (request.finalAverage() != null) {
            boolean passed = Boolean.TRUE.equals(request.passed());
            enrollment.complete(request.finalAverage(), passed);
            log.info("Enrollment completed — enrollmentId: {}, passed: {}", enrollmentId, passed);
        }

        // Baja
        if (request.withdrawalReasonId() != null) {
            enrollment.withdraw(
                    WithdrawalReasonId.of(request.withdrawalReasonId()),
                    request.withdrawalObservations()
            );
            log.info("Enrollment withdrawn — enrollmentId: {}", enrollmentId);
        }

        StudentEnrollment saved = enrollmentRepository.save(enrollment);
        return mapper.toEnrollmentResponse(saved);
    }
}
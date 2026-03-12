package org.school.management.students.enrollment.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.students.enrollment.application.dto.response.EnrollmentResponse;
import org.school.management.students.enrollment.application.mapper.StudentEnrollmentApplicationMapper;
import org.school.management.students.enrollment.domain.exception.EnrollmentNotFoundException;
import org.school.management.students.enrollment.domain.repository.StudentEnrollmentRepository;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Retorna la inscripción activa del estudiante en el año académico indicado.
 * Lanza EnrollmentNotFoundException si no existe inscripción para ese año.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GetActiveEnrollmentUseCase {

    private final StudentEnrollmentRepository enrollmentRepository;
    private final StudentEnrollmentApplicationMapper mapper;

    public EnrollmentResponse execute(UUID studentId, UUID academicYearId) {
        log.debug("Fetching active enrollment — studentId: {}, academicYearId: {}",
                studentId, academicYearId);

        return enrollmentRepository
                .findByStudentIdAndAcademicYearId(
                        StudentPersonalDataId.from(studentId),
                        AcademicYearId.from(academicYearId)
                )
                .map(mapper::toEnrollmentResponse)
                .orElseThrow(() -> EnrollmentNotFoundException.byStudentAndYear(
                        studentId, academicYearId
                ));
    }
}
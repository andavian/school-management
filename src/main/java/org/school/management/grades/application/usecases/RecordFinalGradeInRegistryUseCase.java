package org.school.management.grades.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.domain.valueobject.ids.RegistryId;
import org.school.management.grades.application.dto.response.FinalGradeResponse;
import org.school.management.grades.application.mapper.GradesApplicationMapper;
import org.school.management.grades.domain.exception.GradeAlreadyRecordedInRegistryException;
import org.school.management.grades.domain.exception.GradeNotFoundException;
import org.school.management.grades.domain.exception.InvalidGradeException;
import org.school.management.grades.domain.model.FinalGrade;
import org.school.management.grades.domain.repository.FinalGradeRepository;
import org.school.management.grades.domain.valueobject.FinalGradeId;
import org.school.management.students.records.application.dto.response.StudentRecordResponse;
import org.school.management.students.records.application.usecases.GetRecordByStudentIdUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RecordFinalGradeInRegistryUseCase {

    private final FinalGradeRepository finalGradeRepository;
    private final GetRecordByStudentIdUseCase getRecordByStudentIdUseCase;
    private final GradesApplicationMapper mapper;

    public FinalGradeResponse execute(UUID finalGradeId, UUID studentId) {
        log.debug("Recording final grade in registry — finalGrade: {} student: {}",
                finalGradeId, studentId);

        // 1. Obtener la nota final
        FinalGrade finalGrade = finalGradeRepository
                .findById(FinalGradeId.from(finalGradeId))
                .orElseThrow(() -> GradeNotFoundException.finalGrade(finalGradeId));

        // 2. Guards de dominio
        if (finalGrade.isRecordedInRegistry()) {
            throw GradeAlreadyRecordedInRegistryException.forFinalGrade(finalGradeId);
        }

        if (!finalGrade.isValidated()) {
            throw InvalidGradeException.withReason(
                    "Cannot record unvalidated final grade in registry: " + finalGradeId
            );
        }

        // 3. Obtener el legajo del alumno — cruce via use case público de students/
        StudentRecordResponse record = getRecordByStudentIdUseCase.execute(studentId);

        if (record.registryId() == null || record.folioNumber() == null) {
            throw InvalidGradeException.withReason(
                    "Student record has no registry or folio assigned for studentId: "
                            + studentId
            );
        }

        // 4. Registrar en el folio ya asignado al alumno
        FinalGrade recorded = finalGrade.recordInRegistry(
                RegistryId.from(record.registryId()),
                record.folioNumber()
        );

        FinalGrade saved = finalGradeRepository.save(recorded);

        log.info("Final grade recorded — registry: {} folio: {} finalGrade: {}",
                record.registryId(), record.folioNumber(), finalGradeId);

        return mapper.toFinalGradeResponse(saved);
    }
}

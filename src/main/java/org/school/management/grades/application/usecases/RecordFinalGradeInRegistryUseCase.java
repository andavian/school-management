package org.school.management.grades.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.domain.service.FolioAssignmentService;
import org.school.management.academic.domain.valueobject.ids.RegistryId;
import org.school.management.grades.application.dto.response.FinalGradeResponse;
import org.school.management.grades.application.mapper.GradesApplicationMapper;
import org.school.management.grades.domain.exception.GradeAlreadyRecordedInRegistryException;
import org.school.management.grades.domain.exception.GradeNotFoundException;
import org.school.management.grades.domain.exception.InvalidGradeException;
import org.school.management.grades.domain.model.FinalGrade;
import org.school.management.grades.domain.repository.FinalGradeRepository;
import org.school.management.grades.domain.valueobject.FinalGradeId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RecordFinalGradeInRegistryUseCase {

    private final FinalGradeRepository finalGradeRepository;
    private final FolioAssignmentService folioAssignmentService;
    private final GradesApplicationMapper mapper;

    public FinalGradeResponse execute(UUID finalGradeId, UUID validatedBy) {
        log.debug("Recording final grade in registry: {}", finalGradeId);

        FinalGrade finalGrade = finalGradeRepository
                .findById(FinalGradeId.from(finalGradeId))
                .orElseThrow(() -> GradeNotFoundException.finalGrade(finalGradeId));

        if (finalGrade.isRecordedInRegistry()) {
            throw GradeAlreadyRecordedInRegistryException.forFinalGrade(finalGradeId);
        }

        if (!finalGrade.isValidated()) {
            throw InvalidGradeException.withReason(
                    "Cannot record unvalidated final grade in registry: " + finalGradeId
            );
        }

        // FolioAssignmentService busca el registro activo internamente — patrón del proyecto
        int folioNumber = folioAssignmentService.assignNextFolio();
        RegistryId registryId = folioAssignmentService.getCurrentRegistryId();

        FinalGrade recorded = finalGrade.recordInRegistry(registryId, folioNumber);
        FinalGrade saved = finalGradeRepository.save(recorded);

        log.info("Final grade recorded in registry: {} folio: {} for finalGrade: {}",
                registryId.asString(), folioNumber, finalGradeId);

        return mapper.toFinalGradeResponse(saved);
    }
}

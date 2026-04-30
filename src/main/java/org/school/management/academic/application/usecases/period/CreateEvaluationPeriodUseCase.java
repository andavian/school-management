package org.school.management.academic.application.usecases.period;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.request.CreateEvaluationPeriodRequest;
import org.school.management.academic.application.dto.response.EvaluationPeriodResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.EvaluationPeriodOverlapException;
import org.school.management.academic.domain.exception.AcademicYearNotFoundException;
import org.school.management.academic.domain.model.EvaluationPeriod;
import org.school.management.academic.domain.repository.AcademicYearRepository;
import org.school.management.academic.domain.repository.EvaluationPeriodRepository;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateEvaluationPeriodUseCase {

    private final EvaluationPeriodRepository evaluationPeriodRepository;
    private final AcademicYearRepository academicYearRepository;
    private final AcademicApplicationMapper mapper;

    public EvaluationPeriodResponse execute(String academicYearIdStr, CreateEvaluationPeriodRequest request) {
        log.info("Creating evaluation period {} for academic year {}", request.periodNumber(), academicYearIdStr);

        AcademicYearId academicYearId = AcademicYearId.from(academicYearIdStr);

        // 1. Validar que el año académico existe
        academicYearRepository.findByAcademicYearId(academicYearId)
                .orElseThrow(() -> new AcademicYearNotFoundException(
                        "Academic year not found: " + academicYearIdStr));

        // 2. Validar que no exista ya un período con ese número en ese año
        if (evaluationPeriodRepository.existsByAcademicYearAndPeriodNumber(academicYearId, request.periodNumber())) {
            throw new EvaluationPeriodOverlapException(
                    "Period number " + request.periodNumber() + " already exists for academic year " + academicYearIdStr);
        }

        // 3. Validar fechas (redundante con dominio pero da mejor mensaje)
        if (request.endDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        // 4. Crear el período
        EvaluationPeriod period = EvaluationPeriod.create(
                academicYearId,
                request.periodNumber(),
                request.name(),
                request.startDate(),
                request.endDate()
        );

        // 5. Guardar
        EvaluationPeriod saved = evaluationPeriodRepository.save(period);

        log.info("Evaluation period created successfully: {} (ID: {})", saved.getName(), saved.getPeriodId());
        return mapper.toEvaluationPeriodResponse(saved);
    }
}
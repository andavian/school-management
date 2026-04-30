package org.school.management.academic.application.usecases.period;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.response.EvaluationPeriodResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.AcademicYearNotFoundException;
import org.school.management.academic.domain.exception.EvaluationPeriodNotFoundException;
import org.school.management.academic.domain.model.EvaluationPeriod;
import org.school.management.academic.domain.repository.AcademicYearRepository;
import org.school.management.academic.domain.repository.EvaluationPeriodRepository;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetCurrentEvaluationPeriodUseCase {

    private final EvaluationPeriodRepository evaluationPeriodRepository;
    private final AcademicYearRepository academicYearRepository;
    private final AcademicApplicationMapper mapper;

    public EvaluationPeriodResponse execute(String academicYearIdStr) {
        log.debug("Getting current evaluation period for academic year: {}", academicYearIdStr);

        AcademicYearId academicYearId = AcademicYearId.from(academicYearIdStr);

        // Validar que el año académico existe
        academicYearRepository.findByAcademicYearId(academicYearId)
                .orElseThrow(() -> new AcademicYearNotFoundException(
                        "Academic year not found: " + academicYearIdStr));

        EvaluationPeriod period = evaluationPeriodRepository.findCurrentPeriod(academicYearId)
                .orElseThrow(() -> new EvaluationPeriodNotFoundException(
                        "No active current period found for academic year: " + academicYearIdStr));

        return mapper.toEvaluationPeriodResponse(period);
    }
}
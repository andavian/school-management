package org.school.management.academic.application.usecases.period;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.response.EvaluationPeriodResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.EvaluationPeriodNotFoundException;
import org.school.management.academic.domain.model.EvaluationPeriod;
import org.school.management.academic.domain.repository.EvaluationPeriodRepository;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetEvaluationPeriodUseCase {

    private final EvaluationPeriodRepository evaluationPeriodRepository;
    private final AcademicApplicationMapper mapper;

    public EvaluationPeriodResponse execute(String periodIdStr) {
        log.debug("Getting evaluation period: {}", periodIdStr);

        PeriodId periodId = PeriodId.from(periodIdStr);
        EvaluationPeriod period = evaluationPeriodRepository.findById(periodId)
                .orElseThrow(() -> EvaluationPeriodNotFoundException.byId(periodIdStr));

        return mapper.toEvaluationPeriodResponse(period);
    }
}
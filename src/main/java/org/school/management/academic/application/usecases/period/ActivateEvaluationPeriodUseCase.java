package org.school.management.academic.application.usecases.period;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.response.EvaluationPeriodResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.EvaluationPeriodClosedException;
import org.school.management.academic.domain.exception.EvaluationPeriodNotFoundException;
import org.school.management.academic.domain.model.EvaluationPeriod;
import org.school.management.academic.domain.repository.EvaluationPeriodRepository;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ActivateEvaluationPeriodUseCase {

    private final EvaluationPeriodRepository evaluationPeriodRepository;
    private final AcademicApplicationMapper mapper;

    public EvaluationPeriodResponse execute(String periodIdStr) {
        log.info("Activating evaluation period: {}", periodIdStr);

        PeriodId periodId = PeriodId.from(periodIdStr);
        EvaluationPeriod period = evaluationPeriodRepository.findById(periodId)
                .orElseThrow(() -> EvaluationPeriodNotFoundException.byId(periodIdStr));

        if (period.getStatus() == org.school.management.academic.domain.valueobject.enums.PeriodStatus.CLOSED) {
            throw new EvaluationPeriodClosedException("Cannot activate a closed period: " + periodIdStr);
        }

        EvaluationPeriod activated = period.activate();
        EvaluationPeriod saved = evaluationPeriodRepository.save(activated);

        log.info("Evaluation period activated successfully: {}", saved.getPeriodId());
        return mapper.toEvaluationPeriodResponse(saved);
    }
}
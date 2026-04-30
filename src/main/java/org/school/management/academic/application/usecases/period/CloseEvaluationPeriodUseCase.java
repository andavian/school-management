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
@Transactional
public class CloseEvaluationPeriodUseCase {

    private final EvaluationPeriodRepository evaluationPeriodRepository;
    private final AcademicApplicationMapper mapper;

    public EvaluationPeriodResponse execute(String periodIdStr) {
        log.info("Closing evaluation period: {}", periodIdStr);

        PeriodId periodId = PeriodId.from(periodIdStr);
        EvaluationPeriod period = evaluationPeriodRepository.findById(periodId)
                .orElseThrow(() -> EvaluationPeriodNotFoundException.byId(periodIdStr));

        EvaluationPeriod closed = period.close();
        EvaluationPeriod saved = evaluationPeriodRepository.save(closed);

        log.info("Evaluation period closed successfully: {}", saved.getPeriodId());
        return mapper.toEvaluationPeriodResponse(saved);
    }
}
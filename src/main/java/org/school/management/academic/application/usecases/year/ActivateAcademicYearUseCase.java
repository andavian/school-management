package org.school.management.academic.application.usecases.year;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.response.AcademicYearResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.model.AcademicYear;
import org.school.management.academic.domain.service.AcademicYearActivationService;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ActivateAcademicYearUseCase {

    private final AcademicYearActivationService activationService;
    private final AcademicApplicationMapper mapper;

    public AcademicYearResponse execute(String academicYearId) {
        log.info("Activating academic year: {}", academicYearId);

        AcademicYearId id = new AcademicYearId(java.util.UUID.fromString(academicYearId));
        AcademicYear activated = activationService.activateYear(id);

        log.info("Academic year activated: {}", activated.getYearValue());
        return mapper.toAcademicYearResponse(activated);
    }
}

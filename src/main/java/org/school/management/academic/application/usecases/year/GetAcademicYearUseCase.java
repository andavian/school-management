package org.school.management.academic.application.usecases.year;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.response.AcademicYearResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.AcademicYearNotFoundException;
import org.school.management.academic.domain.model.AcademicYear;
import org.school.management.academic.domain.repository.AcademicYearRepository;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetAcademicYearUseCase {

    private final AcademicYearRepository academicYearRepository;
    private final AcademicApplicationMapper mapper;

    public AcademicYearResponse execute(String academicYearId) {
        log.debug("Getting academic year: {}", academicYearId);

        AcademicYear academicYear = academicYearRepository
                .findById(new AcademicYearId(java.util.UUID.fromString(academicYearId)))
                .orElseThrow(() -> new AcademicYearNotFoundException(
                        "Academic year not found: " + academicYearId
                ));

        return mapper.toAcademicYearResponse(academicYear);
    }
}

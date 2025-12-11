package org.school.management.academic.application.usecases.grade_level;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.response.GradeLevelResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.model.GradeLevel;
import org.school.management.academic.domain.repository.GradeLevelRepository;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ListGradeLevelsUseCase {

    private final GradeLevelRepository gradeLevelRepository;
    private final AcademicApplicationMapper mapper;

    public List<GradeLevelResponse> execute(String academicYearId, Boolean activeOnly) {
        log.debug("Listing grade levels for year: {} (activeOnly: {})",
                academicYearId, activeOnly);

        AcademicYearId yearId = academicYearId != null
                ? new AcademicYearId(UUID.fromString(academicYearId))
                : null;

        List<GradeLevel> gradeLevels;

        if (yearId != null) {
            gradeLevels = Boolean.TRUE.equals(activeOnly)
                    ? gradeLevelRepository.findActiveByAcademicYear(yearId)
                    : gradeLevelRepository.findByAcademicYear(yearId);
        } else {
            gradeLevels = gradeLevelRepository.findCurrentYearActiveLevels();
        }

        return gradeLevels.stream()
                .map(mapper::toGradeLevelResponse)
                .collect(Collectors.toList());
    }
}

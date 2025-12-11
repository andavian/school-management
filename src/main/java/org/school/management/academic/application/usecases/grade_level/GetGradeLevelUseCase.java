package org.school.management.academic.application.usecases.grade_level;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.response.GradeLevelResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.GradeLevelNotFoundException;
import org.school.management.academic.domain.model.GradeLevel;
import org.school.management.academic.domain.repository.GradeLevelRepository;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetGradeLevelUseCase {

    private final GradeLevelRepository gradeLevelRepository;
    private final AcademicApplicationMapper mapper;

    public GradeLevelResponse execute(String gradeLevelId) {
        log.debug("Getting grade level: {}", gradeLevelId);

        GradeLevel gradeLevel = gradeLevelRepository
                .findById(new GradeLevelId(UUID.fromString(gradeLevelId)))
                .orElseThrow(() -> new GradeLevelNotFoundException(
                        "Grade level not found: " + gradeLevelId
                ));

        return mapper.toGradeLevelResponse(gradeLevel);
    }
}

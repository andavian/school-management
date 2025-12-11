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
@Transactional
public class DeactivateGradeLevelUseCase {

    private final GradeLevelRepository gradeLevelRepository;
    private final AcademicApplicationMapper mapper;

    public GradeLevelResponse execute(String gradeLevelId) {
        log.info("Deactivating grade level: {}", gradeLevelId);

        // 1. Buscar curso
        GradeLevelId id = new GradeLevelId(UUID.fromString(gradeLevelId));
        GradeLevel gradeLevel = gradeLevelRepository.findById(id)
                .orElseThrow(() -> new GradeLevelNotFoundException(
                        "Grade level not found: " + gradeLevelId
                ));

        // 2. Desactivar
        GradeLevel deactivated = gradeLevel.withIsActive(false);

        // 3. Guardar
        GradeLevel saved = gradeLevelRepository.save(deactivated);

        log.info("Grade level deactivated successfully");
        return mapper.toGradeLevelResponse(saved);
    }
}

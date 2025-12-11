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
public class AssignHomeroomTeacherUseCase {

    private final GradeLevelRepository gradeLevelRepository;
    private final AcademicApplicationMapper mapper;

    public GradeLevelResponse execute(String gradeLevelId, String teacherId) {
        log.info("Assigning homeroom teacher {} to grade level {}", teacherId, gradeLevelId);

        // 1. Buscar curso
        GradeLevelId id = new GradeLevelId(UUID.fromString(gradeLevelId));
        GradeLevel gradeLevel = gradeLevelRepository.findById(id)
                .orElseThrow(() -> new GradeLevelNotFoundException(
                        "Grade level not found: " + gradeLevelId
                ));

        // 2. Asignar profesor (validación de que el profesor existe se hace en otro módulo)
        UUID teacherUUID = UUID.fromString(teacherId);
        GradeLevel updated = gradeLevel.assignHomeroomTeacher(teacherUUID);

        // 3. Guardar
        GradeLevel saved = gradeLevelRepository.save(updated);

        log.info("Homeroom teacher assigned successfully");
        return mapper.toGradeLevelResponse(saved);
    }
}

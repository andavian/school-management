package org.school.management.academic.application.usecases.subject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.response.SubjectResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.GradeLevelNotFoundException;
import org.school.management.academic.domain.model.GradeLevel;
import org.school.management.academic.domain.model.Subject;
import org.school.management.academic.domain.repository.GradeLevelRepository;
import org.school.management.academic.domain.repository.SubjectRepository;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ListSubjectsForGradeLevelUseCase {

    private final SubjectRepository subjectRepository;
    private final GradeLevelRepository gradeLevelRepository;
    private final AcademicApplicationMapper mapper;

    public List<SubjectResponse> execute(String gradeLevelId) {
        log.debug("Listing subjects for grade level: {}", gradeLevelId);

        // 1. Buscar el curso
        GradeLevelId id = new GradeLevelId(UUID.fromString(gradeLevelId));
        GradeLevel gradeLevel = gradeLevelRepository.findById(id)
                .orElseThrow(() -> new GradeLevelNotFoundException(
                        "Grade level not found: " + gradeLevelId
                ));

        // 2. Obtener materias disponibles (comunes + de la orientación)
        List<Subject> subjects = subjectRepository.findAvailableForGradeLevel(
                gradeLevel.getYearLevel(),
                gradeLevel.getOrientationId()
        );

        return subjects.stream()
                .map(mapper::toSubjectResponse)
                .collect(Collectors.toList());
    }
}

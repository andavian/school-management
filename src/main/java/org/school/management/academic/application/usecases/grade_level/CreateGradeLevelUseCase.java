package org.school.management.academic.application.usecases.grade_level;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.request.*;
import org.school.management.academic.application.dto.response.*;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.*;
import org.school.management.academic.domain.model.*;
import org.school.management.academic.domain.repository.*;
import org.school.management.academic.domain.service.*;
import org.school.management.academic.domain.valueobject.*;
import org.school.management.academic.domain.valueobject.enums.*;
import org.school.management.academic.domain.valueobject.ids.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

// ============================================================================
// GRADE LEVEL USE CASES
// ============================================================================

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateGradeLevelUseCase {

    private final GradeLevelRepository gradeLevelRepository;
    private final AcademicYearRepository academicYearRepository;
    private final GradeLevelValidationService validationService;
    private final AcademicApplicationMapper mapper;

    public GradeLevelResponse execute(CreateGradeLevelRequest request) {
        log.info("Creating grade level: {}{}", request.yearLevel(), request.division());

        // 1. Validar que el año académico exista
        AcademicYearId academicYearId = new AcademicYearId(
                UUID.fromString(request.academicYearId())
        );

        academicYearRepository.findById(academicYearId)
                .orElseThrow(() -> new AcademicYearNotFoundException(
                        "Academic year not found: " + request.academicYearId()
                ));

        // 2. Validar la creación del curso
        OrientationId orientationId = request.orientationId() != null
                ? new OrientationId(UUID.fromString(request.orientationId()))
                : null;

        validationService.validateGradeLevelCreation(
                academicYearId,
                YearLevel.of(request.yearLevel()),
                Division.of(request.division()),
                orientationId
        );

        // 3. Crear el curso
        GradeLevel gradeLevel = GradeLevel.create(
                academicYearId,
                request.yearLevel(),
                request.division(),
                orientationId,
                Shift.valueOf(request.shift()),
                request.maxStudents()
        );

        // 4. Guardar
        GradeLevel saved = gradeLevelRepository.save(gradeLevel);

        log.info("Grade level created successfully: {}", saved.getDisplayName());
        return mapper.toGradeLevelResponse(saved);
    }
}



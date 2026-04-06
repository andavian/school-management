package org.school.management.teachingmaterials.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.teachingmaterials.application.dto.response.TeachingMaterialResponse;
import org.school.management.teachingmaterials.application.mapper.TeachingMaterialApplicationMapper;
import org.school.management.teachingmaterials.domain.repository.TeachingMaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Caso de uso: listar los materiales visibles de los cursos en que está inscripto el estudiante.
 *
 * <p>El controller es responsable de obtener los IDs de los cursos del estudiante
 * (via {@code GetStudentCoursesUseCase} de {@code course/}) y pasarlos aquí.
 * Este use case solo filtra materiales visibles por esos IDs — no consulta inscripciones.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GetMaterialsForStudentUseCase {

    private final TeachingMaterialRepository materialRepository;
    private final TeachingMaterialApplicationMapper mapper;

    public List<TeachingMaterialResponse> execute(List<UUID> courseSubjectIds) {
        log.debug("Getting visible materials for {} courses", courseSubjectIds.size());

        if (courseSubjectIds == null || courseSubjectIds.isEmpty()) {
            return List.of();
        }

        List<CourseSubjectId> courseSubjectVOs = courseSubjectIds.stream()
                .map(CourseSubjectId::of)
                .toList();

        return materialRepository
                .findVisibleByCourseSubjectIds(courseSubjectVOs)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
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
 * Caso de uso: listar todos los materiales de un course-subject.
 * Usado por TEACHER, ADMIN y STAFF — ve todos, incluyendo los no visibles.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GetMaterialsByCourseUseCase {

    private final TeachingMaterialRepository materialRepository;
    private final TeachingMaterialApplicationMapper mapper;

    public List<TeachingMaterialResponse> execute(UUID courseSubjectId) {
        log.debug("Getting materials for courseSubject: {}", courseSubjectId);

        return materialRepository
                .findByCourseSubjectId(CourseSubjectId.of(courseSubjectId))
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
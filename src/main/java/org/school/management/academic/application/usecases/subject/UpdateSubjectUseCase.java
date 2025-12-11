package org.school.management.academic.application.usecases.subject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.request.UpdateSubjectRequest;
import org.school.management.academic.application.dto.response.SubjectResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.SubjectNotFoundException;
import org.school.management.academic.domain.model.Subject;
import org.school.management.academic.domain.repository.SubjectRepository;
import org.school.management.academic.domain.valueobject.ids.SubjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateSubjectUseCase {

    private final SubjectRepository subjectRepository;
    private final AcademicApplicationMapper mapper;

    public SubjectResponse execute(String subjectId, UpdateSubjectRequest request) {
        log.info("Updating subject: {}", subjectId);

        // 1. Buscar materia
        SubjectId id = new SubjectId(UUID.fromString(subjectId));
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new SubjectNotFoundException(
                        "Subject not found: " + subjectId
                ));

        // 2. Actualizar campos
        Subject updated = subject.toBuilder()
                .name(request.name() != null ? request.name() : subject.getName())
                .description(request.description() != null ?
                        request.description() : subject.getDescription())
                .weeklyHours(request.weeklyHours() != null ?
                        request.weeklyHours() : subject.getWeeklyHours())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        // 3. Guardar
        Subject saved = subjectRepository.save(updated);

        log.info("Subject updated successfully: {}", saved.getName());
        return mapper.toSubjectResponse(saved);
    }
}

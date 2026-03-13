package org.school.management.students.parents.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.students.parents.application.dto.response.StudentParentResponse;
import org.school.management.students.parents.application.mapper.ParentApplicationMapper;
import org.school.management.students.parents.domain.exception.ParentNotFoundException;
import org.school.management.students.parents.domain.repository.ParentRepository;
import org.school.management.students.parents.domain.repository.StudentParentRepository;
import org.school.management.students.parents.domain.valueobject.ParentId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Retorna todos los padres/tutores vinculados a un estudiante.
 * Incluye los datos del padre embebidos en cada vínculo.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GetParentsByStudentIdUseCase {

    private final StudentParentRepository studentParentRepository;
    private final ParentRepository parentRepository;
    private final ParentApplicationMapper mapper;

    public List<StudentParentResponse> execute(UUID studentId) {
        log.debug("Fetching parents for studentId: {}", studentId);

        return studentParentRepository
                .findAllByStudentId(StudentPersonalDataId.from(studentId))
                .stream()
                .map(studentParent -> {
                    var parent = parentRepository
                            .findByParentId(ParentId.of(studentParent.getParentId().value()))
                            .orElseThrow(() -> ParentNotFoundException.byId(
                                    studentParent.getParentId().value()
                            ));
                    return mapper.toStudentParentResponse(studentParent, parent);
                })
                .toList();
    }
}
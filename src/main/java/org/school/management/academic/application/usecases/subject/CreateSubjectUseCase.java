package org.school.management.academic.application.usecases.subject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.request.CreateSubjectRequest;
import org.school.management.academic.application.dto.response.SubjectResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.OrientationNotFoundException;
import org.school.management.academic.domain.exception.SubjectAlreadyExistsException;
import org.school.management.academic.domain.model.Subject;
import org.school.management.academic.domain.repository.OrientationRepository;
import org.school.management.academic.domain.repository.SubjectRepository;
import org.school.management.academic.domain.valueobject.ids.OrientationId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateSubjectUseCase {

    private final SubjectRepository subjectRepository;
    private final OrientationRepository orientationRepository;
    private final AcademicApplicationMapper mapper;

    public SubjectResponse execute(CreateSubjectRequest request) {
        log.info("Creating subject: {}", request.name());

        // 1. Validar que no exista el código
        if (subjectRepository.existsByCode(request.code())) {
            throw new SubjectAlreadyExistsException(
                    "Subject with code " + request.code() + " already exists"
            );
        }

        // 2. Si tiene orientación, validar que exista
        OrientationId orientationId = null;
        if (request.orientationId() != null) {
            orientationId = new OrientationId(UUID.fromString(request.orientationId()));
            orientationRepository.findById(orientationId)
                    .orElseThrow(() -> new OrientationNotFoundException(
                            "Orientation not found: " + request.orientationId()
                    ));
        }


        // 3. Crear la materia
        Subject subject = Subject.create(
                request.name(),
                request.code(),
                request.yearLevel(),
                orientationId,
                request.isMandatory(),
                request.weeklyHours(),
                request.description()
        );

        // 4. Guardar
        Subject saved = subjectRepository.save(subject);

        log.info("Subject created successfully: {}", saved.getName());
        return mapper.toSubjectResponse(saved);
    }
}

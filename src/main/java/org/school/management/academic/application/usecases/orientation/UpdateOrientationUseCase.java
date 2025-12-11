package org.school.management.academic.application.usecases.orientation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.request.UpdateOrientationRequest;
import org.school.management.academic.application.dto.response.OrientationResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.OrientationNotFoundException;
import org.school.management.academic.domain.model.Orientation;
import org.school.management.academic.domain.repository.OrientationRepository;
import org.school.management.academic.domain.valueobject.ids.OrientationId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateOrientationUseCase {

    private final OrientationRepository orientationRepository;
    private final AcademicApplicationMapper mapper;

    public OrientationResponse execute(String orientationId, UpdateOrientationRequest request) {
        log.info("Updating orientation: {}", orientationId);

        // 1. Buscar orientación existente
        OrientationId id = new OrientationId(java.util.UUID.fromString(orientationId));
        Orientation orientation = orientationRepository.findById(id)
                .orElseThrow(() -> new OrientationNotFoundException(
                        "Orientation not found: " + orientationId
                ));

        // 2. Actualizar campos
        Orientation updated = orientation.toBuilder()
                .name(request.name() != null ? request.name() : orientation.getName())
                .description(request.description() != null ?
                        request.description() : orientation.getDescription())
                .availableFromYear((request.availableFromYear() != null ?
                                        request.availableFromYear() : orientation.getAvailableFromYear()))
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        // 3. Guardar
        Orientation saved = orientationRepository.save(updated);

        log.info("Orientation updated successfully: {}", saved.getName());
        return mapper.toOrientationResponse(saved);
    }
}

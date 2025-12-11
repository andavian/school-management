package org.school.management.academic.application.usecases.orientation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class ToggleOrientationStatusUseCase {

    private final OrientationRepository orientationRepository;
    private final AcademicApplicationMapper mapper;

    public OrientationResponse execute(String orientationId) {
        log.info("Toggling orientation status: {}", orientationId);

        // 1. Buscar orientación
        OrientationId id = new OrientationId(java.util.UUID.fromString(orientationId));
        Orientation orientation = orientationRepository.findById(id)
                .orElseThrow(() -> new OrientationNotFoundException(
                        "Orientation not found: " + orientationId
                ));

        // 2. Toggle status
        Orientation toggled = orientation.getIsActive()
                ? orientation.deactivate()
                : orientation.activate();

        // 3. Guardar
        Orientation saved = orientationRepository.save(toggled);

        log.info("Orientation status toggled: {} (active: {})",
                saved.getName(), saved.getIsActive());
        return mapper.toOrientationResponse(saved);
    }
}

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
@Transactional(readOnly = true)
public class GetOrientationUseCase {

    private final OrientationRepository orientationRepository;
    private final AcademicApplicationMapper mapper;

    public OrientationResponse execute(String orientationId) {
        log.debug("Getting orientation: {}", orientationId);

        Orientation orientation = orientationRepository
                .findById(new OrientationId(java.util.UUID.fromString(orientationId)))
                .orElseThrow(() -> new OrientationNotFoundException(
                        "Orientation not found: " + orientationId
                ));

        return mapper.toOrientationResponse(orientation);
    }
}

package org.school.management.academic.application.usecases.orientation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.response.OrientationResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.model.Orientation;
import org.school.management.academic.domain.repository.OrientationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ListOrientationsUseCase {

    private final OrientationRepository orientationRepository;
    private final AcademicApplicationMapper mapper;

    public List<OrientationResponse> execute(Boolean activeOnly) {
        log.debug("Listing orientations (activeOnly: {})", activeOnly);

        List<Orientation> orientations = Boolean.TRUE.equals(activeOnly)
                ? orientationRepository.findActiveOrientations()
                : orientationRepository.findAll();

        return orientations.stream()
                .map(mapper::toOrientationResponse)
                .collect(Collectors.toList());
    }
}

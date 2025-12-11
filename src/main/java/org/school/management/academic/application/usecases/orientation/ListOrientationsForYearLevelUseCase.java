package org.school.management.academic.application.usecases.orientation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.response.OrientationResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.repository.OrientationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ListOrientationsForYearLevelUseCase {

    private final OrientationRepository orientationRepository;
    private final AcademicApplicationMapper mapper;

    public List<OrientationResponse> execute(Integer yearLevel) {
        log.debug("Listing orientations for year level: {}", yearLevel);

        if (yearLevel < 4 || yearLevel > 7) {
            throw new IllegalArgumentException(
                    "Year level must be between 4 and 7 for orientations"
            );
        }

        return orientationRepository.findByAvailableFromYear(yearLevel).stream()
                .map(mapper::toOrientationResponse)
                .collect(Collectors.toList());
    }
}

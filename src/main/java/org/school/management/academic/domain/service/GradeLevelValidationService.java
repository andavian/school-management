package org.school.management.academic.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.domain.exception.GradeLevelAlreadyExistsException;
import org.school.management.academic.domain.exception.InvalidOrientationForYearLevelException;
import org.school.management.academic.domain.exception.OrientationNotActiveException;
import org.school.management.academic.domain.exception.OrientationNotFoundException;
import org.school.management.academic.domain.model.Orientation;
import org.school.management.academic.domain.repository.GradeLevelRepository;
import org.school.management.academic.domain.repository.OrientationRepository;
import org.school.management.academic.domain.valueobject.Division;
import org.school.management.academic.domain.valueobject.YearLevel;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.OrientationId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GradeLevelValidationService {

    private final GradeLevelRepository gradeLevelRepository;
    private final OrientationRepository orientationRepository;

    /**
     * Valida que se pueda crear un curso con los parámetros dados
     *
     * @throws GradeLevelAlreadyExistsException        si ya existe el curso
     * @throws InvalidOrientationForYearLevelException si la orientación no es válida
     */
    public void validateGradeLevelCreation(
            AcademicYearId academicYearId,
            YearLevel yearLevel,
            Division division,
            OrientationId orientationId) {

        log.debug("Validating grade level creation: {}{} - Orientation: {}",
                yearLevel.getValue(), division.getValue(), orientationId);

        // 1. Verificar que no exista ya
        boolean exists = gradeLevelRepository.existsByAcademicYearAndYearLevelAndDivision(
                academicYearId, yearLevel, division
        );

        if (exists) {
            throw new GradeLevelAlreadyExistsException(
                    String.format("Grade level %d%s already exists for this academic year",
                            yearLevel.getValue(), division.getValue())
            );
        }

        // 2. Validar orientación
        if (yearLevel.requiresOrientation()) {
            if (orientationId == null) {
                throw new InvalidOrientationForYearLevelException(
                        "Year level " + yearLevel.getValue() + " requires an orientation"
                );
            }

            // Verificar que la orientación exista y esté disponible para este año
            Orientation orientation = orientationRepository.findById(orientationId)
                    .orElseThrow(() -> new OrientationNotFoundException(
                            "Orientation not found: " + orientationId
                    ));

            if (!orientation.getIsActive()) {
                throw new OrientationNotActiveException(
                        "Orientation " + orientation.getName() + " is not active"
                );
            }

            if (orientation.getAvailableFromYear().getValue() > yearLevel.getValue()) {
                throw new InvalidOrientationForYearLevelException(
                        String.format("Orientation %s is only available from year %d",
                                orientation.getName(), orientation.getAvailableFromYear().getValue())
                );
            }
        } else {
            // 1°-3° no deben tener orientación
            if (orientationId != null) {
                throw new InvalidOrientationForYearLevelException(
                        "Year level " + yearLevel.getValue() + " should not have an orientation"
                );
            }
        }

        log.debug("Grade level creation validation passed");
    }
}

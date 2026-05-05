package org.school.management.academic.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.domain.model.AcademicYear;
import org.school.management.academic.domain.model.QualificationRegistry;
import org.school.management.academic.domain.repository.AcademicYearRepository;
import org.school.management.academic.domain.repository.QualificationRegistryRepository;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.exception.AcademicYearNotFoundException;
import org.school.management.academic.domain.exception.RegistryAlreadyExistsException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class QualificationRegistryFactory {

    private static final int MAX_FOLIOS = 120;

    private final RegistryNumberGenerator numberGenerator;
    private final AcademicYearRepository academicYearRepository;
    private final QualificationRegistryRepository registryRepository;

    /**
     * Crea un nuevo registro activo con número correlativo global y maxFolios fijo (120).
     *
     * @param academicYearId ID del año académico al que pertenece el libro.
     * @return QualificationRegistry listo para persistir.
     */
    public QualificationRegistry create(AcademicYearId academicYearId) {
        AcademicYear year = academicYearRepository.findByAcademicYearId(academicYearId)
                .orElseThrow(() -> new AcademicYearNotFoundException(
                        "Academic year not found: " + academicYearId));

        String registryNumber = numberGenerator.generate(academicYearId, year.getYearValue());
        log.info("Creating new qualification registry: {}", registryNumber);
        return QualificationRegistry.create(registryNumber, academicYearId, MAX_FOLIOS);
    }

    /**
     * Crea un nuevo registro con un número explícito (seed manual).
     * Solo se permite si el número no existe ya en la base de datos.
     *
     * @param academicYearId  ID del año académico.
     * @param registryNumber  Número de registro en formato REG-AAAA-NNN.
     * @return QualificationRegistry listo para persistir.
     */
    public QualificationRegistry createWithNumber(AcademicYearId academicYearId, String registryNumber) {
        if (registryRepository.existsByRegistryNumber(registryNumber)) {
            throw new RegistryAlreadyExistsException(
                    "Registry number already exists: " + registryNumber);
        }

        AcademicYear year = academicYearRepository.findByAcademicYearId(academicYearId)
                .orElseThrow(() -> new AcademicYearNotFoundException(
                        "Academic year not found: " + academicYearId));

        log.info("Creating qualification registry with explicit number: {}", registryNumber);
        return QualificationRegistry.create(registryNumber, academicYearId, MAX_FOLIOS);
    }
}
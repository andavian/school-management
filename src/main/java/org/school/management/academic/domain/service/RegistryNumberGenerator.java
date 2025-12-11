package org.school.management.academic.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.domain.repository.QualificationRegistryRepository;
import org.school.management.academic.domain.valueobject.RegistryNumber; // Importación crucial
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistryNumberGenerator {

    private final QualificationRegistryRepository registryRepository;

    /**
     * Genera un número de registro único en formato: REG-{año}-{secuencia},
     * delegando el formato al Value Object.
     * * @param academicYearId ID del año académico
     * @param year Valor del año (ej. 2025)
     * @return String con el número de registro generado
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public String generate(AcademicYearId academicYearId, int year) {
        log.debug("Generating registry number for academic year: {}", year);

        long sequenceBaseLong = registryRepository.countByAcademicYear(academicYearId);

        if (sequenceBaseLong > Integer.MAX_VALUE) {
              throw new IllegalStateException("The total number of registries exceeded the maximum capacity.");
        }

        int sequenceBase = (int) sequenceBaseLong;
        int sequence = sequenceBase + 1;


        if (sequence > RegistryNumber.MAX_SEQUENCE_ALLOWED) { // Asumiendo que definiste una constante
            log.error("Registry sequence exceeded max allowed value.");
            // ... lanzar excepción de negocio
        }

        // 2. CONSTRUCCIÓN DEL OBJETO (RESPONSABILIDAD DEL VALUE OBJECT)
        // Delegamos la creación y el formato al VO.
        RegistryNumber registryNumberVo = RegistryNumber.generate(year, sequence);

        // 3. Devolvemos el valor crudo (String) para el consumo en la capa de aplicación/persistencia
        String registryNumber = registryNumberVo.getValue();

        log.info("Generated registry number: {}", registryNumber);
        return registryNumber;
    }
}
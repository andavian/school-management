package org.school.management.academic.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.domain.exception.RegistrySequenceExhaustedException;
import org.school.management.academic.domain.repository.QualificationRegistryRepository;
import org.school.management.academic.domain.valueobject.RegistryNumber;
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
     * Genera un nuevo número de registro correlativo global.
     * Formato: REG-AAAA-NNN (AAAA = año, NNN = secuencia global de 3 dígitos).
     *
     * @param academicYearId ID del año académico (para metadata del año).
     * @param year           Valor del año (ej. 2025) usado en el prefijo.
     * @return String con el número generado.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public String generate(AcademicYearId academicYearId, int year) {
        log.debug("Generating next global registry number for year {}", year);

        // Obtener la máxima secuencia actual de la BD (global)
        int maxSeq = registryRepository.getMaxSequenceNumber();
        int nextSeq = maxSeq + 1;

        if (nextSeq > RegistryNumber.MAX_SEQUENCE_ALLOWED) {
            throw new RegistrySequenceExhaustedException(
                    "Maximum registry sequence (" + RegistryNumber.MAX_SEQUENCE_ALLOWED + ") reached. Cannot generate new number.");
        }

        RegistryNumber registryNumber = RegistryNumber.generate(year, nextSeq);
        log.info("Generated registry number: {}", registryNumber.value());
        return registryNumber.value();
    }

    /**
     * Formatea un número de registro con año y secuencia sin consultar la BD.
     * Útil para el seed inicial.
     */
    public String formatNumber(int year, int sequence) {
        if (sequence <= 0 || sequence > RegistryNumber.MAX_SEQUENCE_ALLOWED) {
            throw new IllegalArgumentException("Sequence must be between 1 and " + RegistryNumber.MAX_SEQUENCE_ALLOWED);
        }
        return RegistryNumber.generate(year, sequence).value();
    }
}
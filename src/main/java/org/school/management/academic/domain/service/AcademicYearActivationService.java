package org.school.management.academic.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.domain.exception.AcademicYearAlreadyActiveException;
import org.school.management.academic.domain.exception.AcademicYearNotFoundException;
import org.school.management.academic.domain.model.AcademicYear;
import org.school.management.academic.domain.model.QualificationRegistry;
import org.school.management.academic.domain.repository.AcademicYearRepository;
import org.school.management.academic.domain.repository.QualificationRegistryRepository;
import org.school.management.academic.domain.valueobject.enums.RegistryStatus;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AcademicYearActivationService {

    private final AcademicYearRepository academicYearRepository;
    private final QualificationRegistryRepository registryRepository;

    /**
     * Activa un año académico y desactiva el anterior si existe
     *
     * @param academicYearId ID del año a activar
     * @throws AcademicYearNotFoundException      si el año no existe
     * @throws AcademicYearAlreadyActiveException si el año ya está activo
     */
    public AcademicYear activateYear(AcademicYearId academicYearId) {
        log.info("Activating academic year: {}", academicYearId);

        // Obtener el año a activar
        AcademicYear yearToActivate = academicYearRepository.findById(academicYearId)
                .orElseThrow(() -> new AcademicYearNotFoundException(
                        "Academic year not found: " + academicYearId
                ));

        // Verificar que no esté ya activo
        if (yearToActivate.isActive()) {
            throw new AcademicYearAlreadyActiveException(
                    "Academic year " + yearToActivate.getYearValue() + " is already active"
            );
        }

        // Desactivar cualquier año actual
        Optional<AcademicYear> currentYear = academicYearRepository.findCurrentYear();
        if (currentYear.isPresent()) {
            AcademicYear deactivated = currentYear.get().close();
            academicYearRepository.save(deactivated);
            log.info("Deactivated previous academic year: {}", deactivated.getYearValue());
        }

        // Activar el nuevo año
        AcademicYear activated = yearToActivate.activate();
        AcademicYear saved = academicYearRepository.save(activated);

        log.info("Successfully activated academic year: {}", saved.getYearValue());
        return saved;
    }

    /**
     * Cierra un año académico y todas sus entidades relacionadas
     *
     * @param academicYearId ID del año a cerrar
     */
    public AcademicYear closeYear(AcademicYearId academicYearId) {
        log.info("Closing academic year: {}", academicYearId);

        AcademicYear year = academicYearRepository.findById(academicYearId)
                .orElseThrow(() -> new AcademicYearNotFoundException(
                        "Academic year not found: " + academicYearId
                ));

        // Cerrar todos los registros de calificaciones de este año
        List<QualificationRegistry> registries =
                registryRepository.findByAcademicYear(academicYearId);

        for (QualificationRegistry registry : registries) {
            if (registry.getStatus() == RegistryStatus.ACTIVE) {
                QualificationRegistry closed = registry.close();
                registryRepository.save(closed);
                log.info("Closed registry: {}", registry.getRegistryNumber());
            }
        }

        // Cerrar el año académico
        AcademicYear closed = year.close();
        AcademicYear saved = academicYearRepository.save(closed);

        log.info("Successfully closed academic year: {}", saved.getYearValue());
        return saved;
    }

    /**
     * Verifica si se puede activar un año académico
     */
    public boolean canActivateYear(AcademicYearId academicYearId) {
        Optional<AcademicYear> yearOpt = academicYearRepository.findById(academicYearId);
        return yearOpt.isPresent() && !yearOpt.get().isActive();
    }

    /**
     * Obtiene el año académico actual
     */
    public Optional<AcademicYear> getCurrentYear() {
        return academicYearRepository.findCurrentYear();
    }
}

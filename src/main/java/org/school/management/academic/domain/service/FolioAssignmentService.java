package org.school.management.academic.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.domain.exception.*;
import org.school.management.academic.domain.model.QualificationRegistry;
import org.school.management.academic.domain.repository.QualificationRegistryRepository;
import org.school.management.academic.domain.valueobject.enums.RegistryStatus;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.RegistryId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class FolioAssignmentService {

    private final QualificationRegistryRepository registryRepository;

    // --- MÉTODOS PÚBLICOS DE ASIGNACIÓN (Transaccionales) ---

    /**
     * Asigna el siguiente folio disponible del registro activo del año académico actual.
     * @return FolioNumber asignado
     * @throws NoActiveRegistryException si no hay registro activo
     * @throws RegistryFullException si el registro está lleno
     */
    @Transactional
    public Integer assignNextFolio() {
        log.debug("Assigning next folio from current year active registry");

        // 1. Obtener el registro activo (lectura)
        QualificationRegistry registry = registryRepository.findActiveInCurrentYear()
                .orElseThrow(() -> new NoActiveRegistryException(
                        "No active qualification registry found for current academic year"
                ));

        // 2. Delegar la asignación atómica.
        return assignFolioFromRegistry(registry);
    }

    /**
     * Asigna el siguiente folio disponible de un registro específico.
     * @param academicYearId ID del año académico
     * @return FolioNumber asignado
     * @throws NoActiveRegistryException si no hay registro activo
     * @throws RegistryFullException si el registro está lleno
     */
    @Transactional
    public Integer assignNextFolioForYear(AcademicYearId academicYearId) {
        log.debug("Assigning next folio for academic year: {}", academicYearId);

        // 1. Obtener el registro activo
        QualificationRegistry registry = registryRepository.findActiveRegistryForYear(academicYearId)
                .orElseThrow(() -> new NoActiveRegistryException(
                        "No active qualification registry found for academic year: " + academicYearId
                ));

        // 2. Delegar la asignación atómica.
        return assignFolioFromRegistry(registry);
    }

    // --- MÉTODOS PRIVADOS DE LÓGICA CRÍTICA ---

    /**
     * Helper: Ejecuta la lógica de asignación ATÓMICA y maneja el cierre del registro.
     * @param registry Registro a usar.
     * @return El número de folio asignado.
     */
    private Integer assignFolioFromRegistry(QualificationRegistry registry) {

        RegistryId registryId = registry.getRegistryId();

        // 1. Validación de Dominio (Estado)
        if (registry.getStatus() != RegistryStatus.ACTIVE) {
            throw new RegistryNotActiveException(
                    "Registry " + registry.getRegistryNumber() + " is not active"
            );
        }

        // 2. Validación de Persistencia (Disponibilidad)
        int availableFolios = registryRepository.getAvailableFolios(registryId);
        if (availableFolios <= 0) {
            throw new RegistryFullException(
                    "Registry " + registry.getRegistryNumber() + " is full."
            );
        }

        // 3. OPERACIÓN CRÍTICA ATÓMICA:
        // Llama al repositorio para que la BD realice el incremento de forma segura.
        // Asumimos que incrementFolio devuelve el folio que fue ASIGNADO (el valor antes del incremento).
        int assignedFolio = registryRepository.incrementFolio(registryId);

        // Si la operación atómica falla (devuelve 0 o -1) porque otro proceso lo llenó antes.
        if (assignedFolio <= 0) {
            throw new RegistryFullException("Registry is full (concurrency check failed).");
        }

        // 4. Lógica de Negocio POST-INCREMENTO: Marcar como lleno si se llenó con este folio.
        int remainingFolios = registryRepository.getAvailableFolios(registryId);

        if (remainingFolios <= 0) {
            // Recargar el objeto para obtener el estado actual y marcarlo como lleno en el dominio
            registryRepository.findById(registryId).ifPresent(updatedRegistry -> {
                QualificationRegistry closed = updatedRegistry.markAsFull();
                registryRepository.save(closed);
                log.warn("Registry {} is now FULL", updatedRegistry.getRegistryNumber());
            });
        }

        log.info("Assigned folio {} from registry {}", assignedFolio, registry.getRegistryNumber());
        return assignedFolio;
    }

    // --- MÉTODOS PÚBLICOS DE CONSULTA (No Transaccionales) ---

    /**
     * Verifica si hay folios disponibles en el año actual.
     */
    public boolean hasAvailableFolios() {
        return registryRepository.findActiveInCurrentYear()
                .map(registry -> registryRepository.getAvailableFolios(registry.getRegistryId()) > 0)
                .orElse(false);
    }

    /**
     * Obtiene la cantidad de folios disponibles en el año actual.
     */
    public int getAvailableFoliosCount() {
        return registryRepository.findActiveInCurrentYear()
                .map(registry -> registryRepository.getAvailableFolios(registry.getRegistryId()))
                .orElse(0);
    }

    /**
     * Verifica si el registro está cerca de llenarse (menos de 50 folios).
     */
    public boolean isRegistryNearFull() {
        return getAvailableFoliosCount() < 50;
    }
}
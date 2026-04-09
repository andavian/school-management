// src/main/java/org/school/management/resources/application/usecases/UpdateUnitStatusUseCase.java
package org.school.management.resources.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.resources.application.dto.request.UpdateUnitStatusRequest;
import org.school.management.resources.application.dto.response.ResourceUnitResponse;
import org.school.management.resources.application.mapper.ResourceApplicationMapper;
import org.school.management.resources.domain.exception.ResourceNotFoundException; // Reutilizamos la de recurso, pero para unidad crearemos una si hace falta
import org.school.management.resources.domain.model.ResourceUnit;
import org.school.management.resources.domain.repository.ResourceUnitRepository;
import org.school.management.resources.domain.valueobject.UnitId;
import org.school.management.resources.domain.valueobject.UnitStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateUnitStatusUseCase {

    private final ResourceUnitRepository resourceUnitRepository;
    private final ResourceApplicationMapper mapper;

    @Transactional
    public ResourceUnitResponse execute(UUID unitId, UpdateUnitStatusRequest request) {

        ResourceUnit unit = resourceUnitRepository.findByUnitId(UnitId.from(unitId))
                .orElseThrow(() -> new IllegalArgumentException("Unidad física no encontrada con ID: " + unitId));

        boolean hasChanges = false;

        // Aplicar transición de estado si se indicó y es diferente
        if (request.unitStatus() != null && request.unitStatus() != unit.getUnitStatus()) {
            applyStatusTransition(unit, request.unitStatus());
            hasChanges = true;
        }

        // Actualizar condición física
        if (request.conditionStatus() != null) {
            unit.updateCondition(request.conditionStatus());
            hasChanges = true;
        }

        // Actualizar notas
        if (request.notes() != null) {
            unit.updateNotes(request.notes());
            hasChanges = true;
        }

        if (hasChanges) {
            ResourceUnit saved = resourceUnitRepository.save(unit);
            log.info("Unidad {} actualizada. Nuevo estado: {}", saved.getUnitCode(), saved.getUnitStatus());
            return mapper.toResourceUnitResponse(saved);
        }

        log.info("No se realizaron cambios en la unidad {}", unitId);
        return mapper.toResourceUnitResponse(unit);
    }

    /**
     * Aplica transiciones de estado delegando la validación al modelo de dominio.
     */
    private void applyStatusTransition(ResourceUnit unit, UnitStatus newStatus) {
        switch (newStatus) {
            case MAINTENANCE -> unit.markForMaintenance();
            case ON_LOAN -> unit.markAsOnLoan();
            case AVAILABLE -> {
                if (unit.getUnitStatus() == UnitStatus.MAINTENANCE) {
                    unit.completeMaintenance();
                } else if (unit.getUnitStatus() == UnitStatus.ON_LOAN) {
                    unit.returnFromLoan();
                } else if (unit.getUnitStatus() == UnitStatus.IN_USE) {
                    unit.returnFromReservation();
                }
            }
            case RETIRED -> unit.retire();
            default -> throw new IllegalArgumentException("Transición de estado no soportada: " + newStatus);
        }
    }
}
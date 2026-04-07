package org.school.management.resources.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.resources.application.dto.request.UpdateUnitStatusRequest;
import org.school.management.resources.application.dto.response.ResourceUnitResponse;
import org.school.management.resources.application.mapper.ResourceApplicationMapper;
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
                .orElseThrow(() -> new IllegalArgumentException("Unidad física no encontrada: " + unitId));

        // Aplicar transiciones de estado si cambió
        if (request.unitStatus() != null && request.unitStatus() != unit.getUnitStatus()) {
            applyStatusTransition(unit, request.unitStatus());
        }

        // Actualizar condición o notas si vienen en el request
        if (request.conditionStatus() != null) {
            unit.updateCondition(request.conditionStatus());
        }
        if (request.notes() != null) {
            unit.updateNotes(request.notes());
        }

        ResourceUnit saved = resourceUnitRepository.save(unit);
        log.info("Estado de unidad {} actualizado a {}", saved.getUnitCode(), saved.getUnitStatus());
        return mapper.toResourceUnitResponse(saved);
    }

    private void applyStatusTransition(ResourceUnit unit, UnitStatus newStatus) {
        // Delegamos la validación de transiciones al modelo de dominio
        switch (newStatus) {
            case MAINTENANCE -> unit.markForMaintenance();
            case ON_LOAN -> unit.markAsOnLoan();
            case AVAILABLE -> {
                if (unit.getUnitStatus() == UnitStatus.MAINTENANCE) unit.completeMaintenance();
                else if (unit.getUnitStatus() == UnitStatus.ON_LOAN) unit.returnFromLoan();
            }
            case RETIRED -> unit.retire();
            default -> throw new IllegalArgumentException("Transición directa no soportada para el estado: " + newStatus);
        }
    }
}
// src/main/java/org/school/management/resources/application/usecases/CreateResourceUnitUseCase.java
package org.school.management.resources.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.resources.application.dto.request.CreateResourceUnitRequest;
import org.school.management.resources.application.dto.response.ResourceUnitResponse;
import org.school.management.resources.application.mapper.ResourceApplicationMapper;
import org.school.management.resources.domain.exception.ResourceNotFoundException;
import org.school.management.resources.domain.model.ResourceUnit;
import org.school.management.resources.domain.repository.ResourceRepository;
import org.school.management.resources.domain.repository.ResourceUnitRepository;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.resources.domain.valueobject.UnitId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateResourceUnitUseCase {

    private final ResourceRepository resourceRepository;
    private final ResourceUnitRepository resourceUnitRepository;
    private final ResourceApplicationMapper mapper;

    @Transactional
    public ResourceUnitResponse execute(CreateResourceUnitRequest request, UUID actorId) {

        // Validar que el recurso padre existe
        ResourceId resourceId = ResourceId.of(request.resourceId());
        resourceRepository.findByResourceId(resourceId)
                .orElseThrow(() -> ResourceNotFoundException.byId(request.resourceId()));

        // Validar unicidad del código de unidad
        if (resourceUnitRepository.existsByUnitCode(request.unitCode())) {
            throw new IllegalArgumentException("Ya existe una unidad con el código: " + request.unitCode());
        }

        // Crear unidad física
        ResourceUnit unit = ResourceUnit.create(
                UnitId.generate(),
                resourceId,
                request.unitCode(),
                request.serialNumber(),
                request.conditionStatus()
        );

        ResourceUnit saved = resourceUnitRepository.save(unit);

        log.info("Unidad física creada: {} para recurso {}", saved.getUnitCode(), resourceId);
        return mapper.toResourceUnitResponse(saved);
    }
}
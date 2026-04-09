// src/main/java/org/school/management/resources/application/usecases/UpdateResourceUseCase.java
package org.school.management.resources.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.resources.application.dto.request.UpdateResourceRequest;
import org.school.management.resources.application.dto.response.ResourceResponse;
import org.school.management.resources.application.mapper.ResourceApplicationMapper;
import org.school.management.resources.domain.exception.ResourceNotFoundException;
import org.school.management.resources.domain.model.Resource;
import org.school.management.resources.domain.repository.ResourceRepository;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateResourceUseCase {

    private final ResourceRepository resourceRepository;
    private final ResourceApplicationMapper mapper;

    @Transactional
    public ResourceResponse execute(UUID resourceId, UpdateResourceRequest request) {

        Resource resource = resourceRepository.findByResourceId(ResourceId.of(resourceId))
                .orElseThrow(() -> ResourceNotFoundException.byId(resourceId));

        // Solo actualizamos si hay cambios (PATCH semántico)
        if (request.hasUpdates()) {
            resource.updateMetadata(
                    request.name(),
                    request.description(),
                    request.location(),
                    request.reservable() != null ? request.reservable() : resource.isReservable(),
                    request.notes()
            );

            Resource saved = resourceRepository.save(resource);
            log.info("Recurso {} actualizado correctamente", resourceId);
            return mapper.toResourceResponse(saved);
        }

        log.info("No se realizaron cambios en el recurso {}", resourceId);
        return mapper.toResourceResponse(resource);
    }
}
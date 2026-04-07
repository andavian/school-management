package org.school.management.resources.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.resources.application.dto.request.UpdateResourceRequest;
import org.school.management.resources.application.dto.response.ResourceResponse;
import org.school.management.resources.application.mapper.ResourceApplicationMapper;
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
                .orElseThrow(() -> new IllegalArgumentException("Recurso no encontrado con ID: " + resourceId));

        // Actualizamos solo si el valor no es nulo (PATCH semántico)
        resource.updateMetadata(
                request.name(),
                request.description(),
                request.location(),
                request.reservable() != null ? request.reservable() : resource.isReservable(),
                request.notes()
        );

        Resource saved = resourceRepository.save(resource);
        log.info("Recurso {} actualizado por el sistema", resourceId);
        return mapper.toResourceResponse(saved);
    }
}
// src/main/java/org/school/management/resources/application/usecases/CreateResourceUseCase.java
package org.school.management.resources.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.resources.application.dto.request.CreateResourceRequest;
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
public class CreateResourceUseCase {

    private final ResourceRepository resourceRepository;
    private final ResourceApplicationMapper mapper;

    @Transactional
    public ResourceResponse execute(CreateResourceRequest request, UUID actorId) {

        if (resourceRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Ya existe un recurso con el código: " + request.code());
        }

        Resource resource = Resource.create(
                ResourceId.generate(),
                request.name(),
                request.code(),
                request.resourceType(),
                request.description(),
                request.location(),
                request.reservable(),
                request.notes(),
                actorId
        );

        Resource saved = resourceRepository.save(resource);

        log.info("Recurso creado: {} ({}) por actor {}",
                saved.getCode(), saved.getResourceType(), actorId);

        return mapper.toResourceResponse(saved);
    }
}
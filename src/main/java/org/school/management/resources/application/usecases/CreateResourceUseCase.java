package org.school.management.resources.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResourceResponse execute(String code, String name, org.school.management.resources.domain.valueobject.ResourceType type,
                                    String description, String location, boolean reservable, String notes, UUID actorId) {
        if (resourceRepository.existsByCode(code)) {
            throw new IllegalArgumentException("Ya existe un recurso con el código: " + code);
        }

        Resource resource = Resource.create(
                ResourceId.generate(),
                name,
                code,
                type,
                description,
                location,
                reservable,
                notes
        );

        Resource saved = resourceRepository.save(resource);
        log.info("Recurso de catálogo creado: {} ({}) por actor {}", saved.getCode(), saved.getResourceType(), actorId);
        return mapper.toResourceResponse(saved);
    }
}
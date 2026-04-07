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
@Transactional
@Slf4j
public class UpdateResourceUseCase {
    private final ResourceRepository resourceRepository;
    private final ResourceApplicationMapper mapper;

    public ResourceResponse execute(UUID resourceId, UpdateResourceRequest request) {
        Resource resource = resourceRepository.findById(ResourceId.of(resourceId))
                .orElseThrow(() -> ResourceNotFoundException.byId(resourceId));
        resource.updateMetadata(request.name(), request.description(), request.resourceType(), request.location());
        Resource saved = resourceRepository.save(resource);
        log.info("Updated resource: {}", saved.getResourceId().value());
        return mapper.toResponse(saved);
    }
}
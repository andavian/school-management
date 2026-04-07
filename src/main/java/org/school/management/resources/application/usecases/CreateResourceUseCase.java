package org.school.management.resources.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.resources.application.dto.request.CreateResourceRequest;
import org.school.management.resources.application.dto.response.ResourceResponse;
import org.school.management.resources.application.mapper.ResourceApplicationMapper;
import org.school.management.resources.domain.model.Resource;
import org.school.management.resources.domain.repository.ResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CreateResourceUseCase {
    private final ResourceRepository resourceRepository;
    private final ResourceApplicationMapper mapper;

    public ResourceResponse execute(CreateResourceRequest request) {
        Resource resource = Resource.create(
                request.name(),
                request.description(),
                request.resourceType(),
                request.location()
        );
        Resource saved = resourceRepository.save(resource);
        log.info("Created resource: {}", saved.getResourceId().value());
        return mapper.toResponse(saved);
    }
}
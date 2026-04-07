package org.school.management.resources.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.resources.application.dto.response.ResourceResponse;
import org.school.management.resources.application.mapper.ResourceApplicationMapper;
import org.school.management.resources.domain.repository.ResourceRepository;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetResourceByIdUseCase {

    private final ResourceRepository resourceRepository;
    private final ResourceApplicationMapper mapper;

    public ResourceResponse execute(UUID resourceId) {
        return resourceRepository.findByResourceId(ResourceId.of(resourceId))
                .map(mapper::toResourceResponse)
                .orElseThrow(() -> new IllegalArgumentException("Recurso no encontrado con ID: " + resourceId));
    }
}
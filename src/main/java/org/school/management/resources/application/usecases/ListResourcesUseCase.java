package org.school.management.resources.application.usecases;

import lombok.RequiredArgsConstructor;
import org.school.management.resources.application.dto.response.ResourceResponse;
import org.school.management.resources.application.mapper.ResourceApplicationMapper;
import org.school.management.resources.domain.repository.ResourceRepository;
import org.school.management.resources.domain.valueobject.ResourceStatus;
import org.school.management.resources.domain.valueobject.ResourceType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListResourcesUseCase {
    private final ResourceRepository resourceRepository;
    private final ResourceApplicationMapper mapper;

    public List<ResourceResponse> execute(ResourceType type, ResourceStatus status) {
        var resources = resourceRepository.findAll();
        if (type != null) {
            resources = resources.stream().filter(r -> r.getResourceType() == type).collect(Collectors.toList());
        }
        if (status != null) {
            resources = resources.stream().filter(r -> r.getStatus() == status).collect(Collectors.toList());
        }
        return resources.stream().map(mapper::toResponse).collect(Collectors.toList());
    }
}
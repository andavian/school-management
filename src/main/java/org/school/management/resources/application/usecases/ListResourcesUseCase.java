package org.school.management.resources.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.resources.application.dto.response.ResourceResponse;
import org.school.management.resources.application.mapper.ResourceApplicationMapper;
import org.school.management.resources.domain.model.Resource;
import org.school.management.resources.domain.repository.ResourceRepository;
import org.school.management.resources.domain.valueobject.ResourceType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ListResourcesUseCase {

    private final ResourceRepository resourceRepository;
    private final ResourceApplicationMapper mapper;

    /**
     * Lista recursos activos. Si se especifica un tipo, filtra por él.
     * Si reservableOnly = true, excluye recursos no reservables.
     */
    public List<ResourceResponse> execute(ResourceType type, boolean reservableOnly) {
        List<Resource> resources = reservableOnly
                ? resourceRepository.findAllActiveAndReservable()
                : resourceRepository.findAllActive();

        List<ResourceResponse> result = resources.stream()
                .filter(r -> type == null || r.getResourceType() == type)
                .map(mapper::toResourceResponse)
                .collect(Collectors.toList());

        log.debug("Listados {} recursos (tipo: {}, soloReservables: {})", result.size(), type, reservableOnly);
        return result;
    }
}
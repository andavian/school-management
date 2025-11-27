package org.school.management.geography.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.geography.application.dto.request.GlobalSearchRequest;
import org.school.management.geography.application.dto.response.PlaceResponse;
import org.school.management.geography.application.mappers.GeographyApplicationMapper;
import org.school.management.geography.domain.model.PlaceWithHierarchy;
import org.school.management.geography.domain.repository.PlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GlobalSearchUseCase {

    private final PlaceRepository placeRepository;
    private final GeographyApplicationMapper mapper;

    public List<PlaceResponse> execute(GlobalSearchRequest request) {
        log.info("Global search: {}", request.searchTerm());

        List<PlaceWithHierarchy> results = placeRepository
                .searchByNameWithHierarchy(request.searchTerm());

        // Limitar resultados si se especificó
        if (request.maxResults() != null && request.maxResults() > 0) {
            results = results.stream()
                    .limit(request.maxResults())
                    .collect(Collectors.toList());
        }

        return results.stream()
                .map(mapper::toPlaceResponse)
                .collect(Collectors.toList());
    }
}


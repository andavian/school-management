package org.school.management.geography.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.geography.application.dto.request.SearchPlacesRequest;
import org.school.management.geography.application.dto.response.PlaceResponse;
import org.school.management.geography.application.mappers.GeographyApplicationMapper;
import org.school.management.geography.domain.model.PlaceWithHierarchy;
import org.school.management.geography.domain.repository.PlaceRepository;
import org.school.management.geography.domain.valueobject.ProvinceId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SearchPlacesUseCase {

    private final PlaceRepository placeRepository;
    private final GeographyApplicationMapper mapper;

    public List<PlaceResponse> execute(SearchPlacesRequest request) {
        log.info("Searching places: query={}, provinceId={}",
                request.query(), request.provinceId());

        List<PlaceWithHierarchy> places;

        if (request.provinceId() != null) {
            // Búsqueda en provincia específica
            places = placeRepository.searchByNameInProvinceWithHierarchy(
                    request.query(),
                    ProvinceId.of(request.provinceId())
            );
        } else {
            // Búsqueda global
            places = placeRepository.searchByNameWithHierarchy(request.query());
        }

        return places.stream()
                .map(mapper::toPlaceResponse)
                .collect(Collectors.toList());
    }
}

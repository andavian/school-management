package org.school.management.geography.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.geography.application.dto.request.ListPlacesByProvinceRequest;
import org.school.management.geography.application.dto.response.PlaceSummaryResponse;
import org.school.management.geography.application.mappers.GeographyApplicationMapper;
import org.school.management.geography.domain.exception.ProvinceNotFoundException;
import org.school.management.geography.domain.model.Place;
import org.school.management.geography.domain.repository.PlaceRepository;
import org.school.management.geography.domain.repository.ProvinceRepository;
import org.school.management.geography.domain.valueobject.PlaceType;
import org.school.management.geography.domain.valueobject.ProvinceId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ListPlacesByProvinceUseCase {

    private final PlaceRepository placeRepository;
    private final ProvinceRepository provinceRepository;
    private final GeographyApplicationMapper mapper;

    public List<PlaceSummaryResponse> execute(ListPlacesByProvinceRequest request) {
        log.info("Listing places for province: {}", request.provinceId());

        ProvinceId provinceId = ProvinceId.of(request.provinceId());

        // Verificar que la provincia existe
        provinceRepository.findById(provinceId)
                .orElseThrow(() -> new ProvinceNotFoundException(provinceId));

        List<Place> places;

        if (request.type() != null) {
            places = placeRepository.findByProvinceIdAndType(
                    provinceId,
                    PlaceType.fromString(request.type())
            );
        } else {
            places = placeRepository.findByProvinceId(provinceId);
        }

        return places.stream()
                .map(mapper::toPlaceSummaryResponse)
                .collect(Collectors.toList());
    }
}
package org.school.management.geography.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.geography.application.dto.request.CreatePlaceRequest;
import org.school.management.geography.application.dto.response.PlaceResponse;
import org.school.management.geography.application.mappers.GeographyApplicationMapper;
import org.school.management.geography.domain.exception.DuplicatePlaceException;
import org.school.management.geography.domain.exception.PlaceNotFoundException;
import org.school.management.geography.domain.exception.ProvinceNotFoundException;
import org.school.management.geography.domain.model.Place;
import org.school.management.geography.domain.model.PlaceWithHierarchy;
import org.school.management.geography.domain.model.Province;
import org.school.management.geography.domain.repository.PlaceRepository;
import org.school.management.geography.domain.repository.ProvinceRepository;
import org.school.management.geography.domain.valueobject.PlaceType;
import org.school.management.geography.domain.valueobject.ProvinceId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatePlaceUseCase {

    private final PlaceRepository placeRepository;
    private final ProvinceRepository provinceRepository;
    private final GeographyApplicationMapper mapper;

    @Transactional
    public PlaceResponse execute(CreatePlaceRequest request) {
        log.info("Creating place: {}", request.name());

        ProvinceId provinceId = ProvinceId.of(request.provinceId());

        // Validar que la provincia existe
        provinceRepository.findById(provinceId)
                .orElseThrow(() -> new ProvinceNotFoundException(provinceId));

        // Validar que no existe lugar con ese nombre en la provincia
        if (placeRepository.existsByNameAndProvince(request.name(), provinceId)) {
            Province province = provinceRepository.findById(provinceId).get();
            throw new DuplicatePlaceException(request.name(), province.getNameAsString());
        }

        // Crear lugar
        Place place = Place.create(
                request.name(),
                PlaceType.fromString(request.type()),
                provinceId,
                request.postalCode()
        );

        place = placeRepository.save(place);

        // Obtener con jerarquía completa
        Place finalPlace = place;
        PlaceWithHierarchy placeWithHierarchy = placeRepository
                .findByIdWithHierarchy(place.getPlaceId())
                .orElseThrow(() -> new PlaceNotFoundException(finalPlace.getPlaceId()));

        log.info("Place created: {}", place.getPlaceId());

        return mapper.toPlaceResponse(placeWithHierarchy);
    }
}


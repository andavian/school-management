package org.school.management.geography.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.geography.application.dto.request.GetPlaceByIdRequest;
import org.school.management.geography.application.dto.response.PlaceResponse;
import org.school.management.geography.application.mappers.GeographyApplicationMapper;
import org.school.management.geography.domain.exception.PlaceNotFoundException;
import org.school.management.geography.domain.model.PlaceWithHierarchy;
import org.school.management.geography.domain.repository.PlaceRepository;
import org.school.management.geography.domain.valueobject.PlaceId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetPlaceByIdUseCase {

    private final PlaceRepository placeRepository;
    private final GeographyApplicationMapper mapper;

    public PlaceResponse execute(GetPlaceByIdRequest request) {
        log.info("Getting place by id: {}", request.placeId());

        PlaceWithHierarchy placeWithHierarchy = placeRepository
                .findByIdWithHierarchy(PlaceId.of(request.placeId()))
                .orElseThrow(() -> new PlaceNotFoundException(PlaceId.of(request.placeId())));

        return mapper.toPlaceResponse(placeWithHierarchy);
    }
}

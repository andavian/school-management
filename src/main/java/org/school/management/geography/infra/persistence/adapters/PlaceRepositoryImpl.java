package org.school.management.geography.infra.persistence.adapters;

import lombok.RequiredArgsConstructor;
import org.school.management.geography.domain.model.Place;
import org.school.management.geography.domain.repository.PlaceRepository;
import org.school.management.geography.domain.valueobject.PlaceId;
import org.school.management.geography.domain.valueobject.ProvinceId;
import org.school.management.geography.infra.persistence.entity.PlaceEntity;
import org.school.management.geography.infra.persistence.repository.PlaceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Adapter implementation
@Repository
@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepository {
    private final PlaceJpaRepository jpaRepository;
    private final PlacePersistenceMapper mapper;

    @Override
    public Place save(Place place) {
        PlaceEntity entity = mapper.toEntity(place);
        PlaceEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Place> findById(PlaceId placeId) {
        return jpaRepository.findById(placeId.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<Place> findByProvinceId(ProvinceId provinceId) {
        return List.of();
    }

    @Override
    public Optional<Place> findByNameAndProvinceId(String name, ProvinceId provinceId) {
        return Optional.empty();
    }

    // ... más métodos
}

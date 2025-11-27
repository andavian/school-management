package org.school.management.geography.infra.persistence.adapters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.geography.domain.model.Place;
import org.school.management.geography.domain.model.PlaceWithHierarchy;
import org.school.management.geography.domain.repository.PlaceRepository;
import org.school.management.geography.domain.valueobject.PlaceId;
import org.school.management.geography.domain.valueobject.PlaceType;
import org.school.management.geography.domain.valueobject.ProvinceId;
import org.school.management.geography.infra.persistence.entity.PlaceEntity;
import org.school.management.geography.infra.persistence.mappers.GeographyPersistenceMapper;
import org.school.management.geography.infra.persistence.repository.CountryJpaRepository;
import org.school.management.geography.infra.persistence.repository.PlaceJpaRepository;
import org.school.management.geography.infra.persistence.repository.ProvinceJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PlaceRepositoryAdapter implements PlaceRepository {

    private final PlaceJpaRepository jpaRepository;
    private final ProvinceJpaRepository provinceJpaRepository;
    private final CountryJpaRepository countryJpaRepository;
    private final GeographyPersistenceMapper mapper;

    @Override
    @Transactional
    public Place save(Place place) {
        log.debug("Saving place: {}", place.getNameAsString());
        PlaceEntity entity = mapper.toPlaceEntity(place);
        PlaceEntity saved = jpaRepository.save(entity);
        return mapper.toPlaceDomain(saved);
    }

    @Override
    public Optional<Place> findById(PlaceId placeId) {
        return jpaRepository.findById(placeId.getValue())
                .map(mapper::toPlaceDomain);
    }

    @Override
    public Optional<Place> findByNameAndProvince(String name, ProvinceId provinceId) {
        return jpaRepository.findByNameAndProvinceId(name, provinceId.getValue())
                .map(mapper::toPlaceDomain);
    }

    @Override
    public Optional<PlaceWithHierarchy> findByIdWithHierarchy(PlaceId placeId) {
        return jpaRepository.findByIdWithHierarchy(placeId.getValue())
                .map(mapper::toPlaceWithHierarchy);
    }

    @Override
    public List<Place> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toPlaceDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Place> findByProvinceId(ProvinceId provinceId) {
        return jpaRepository.findByProvinceId(provinceId.getValue()).stream()
                .map(mapper::toPlaceDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Place> findByType(PlaceType type) {
        PlaceEntity.PlaceTypeEnum entityType = mapper.toPlaceTypeEntity(type);
        return jpaRepository.findByType(entityType).stream()
                .map(mapper::toPlaceDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Place> findByProvinceIdAndType(ProvinceId provinceId, PlaceType type) {
        PlaceEntity.PlaceTypeEnum entityType = mapper.toPlaceTypeEntity(type);
        return jpaRepository.findByProvinceIdAndType(provinceId.getValue(), entityType).stream()
                .map(mapper::toPlaceDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Place> findByPostalCode(String postalCode) {
        return jpaRepository.findByPostalCode(postalCode).stream()
                .map(mapper::toPlaceDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Place> searchByName(String namePattern) {
        return jpaRepository.searchByName(namePattern).stream()
                .map(mapper::toPlaceDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Place> searchByNameInProvince(String namePattern, ProvinceId provinceId) {
        return jpaRepository.searchByNameInProvince(namePattern, provinceId.getValue()).stream()
                .map(mapper::toPlaceDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlaceWithHierarchy> searchByNameWithHierarchy(String namePattern) {
        return jpaRepository.globalSearch(namePattern).stream()
                .map(mapper::toPlaceWithHierarchy)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlaceWithHierarchy> searchByNameInProvinceWithHierarchy(
            String namePattern,
            ProvinceId provinceId
    ) {
        return jpaRepository.searchByNameInProvince(namePattern, provinceId.getValue()).stream()
                .map(PlaceEntity::getPlaceId)
                .map(placeId -> jpaRepository.findByIdWithHierarchy(placeId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(mapper::toPlaceWithHierarchy)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByNameAndProvince(String name, ProvinceId provinceId) {
        return jpaRepository.existsByNameAndProvinceId(name, provinceId.getValue());
    }

    @Override
    public long countByProvince(ProvinceId provinceId) {
        return jpaRepository.countByProvinceId(provinceId.getValue());
    }

    @Override
    public long countByType(PlaceType type) {
        PlaceEntity.PlaceTypeEnum entityType = mapper.toPlaceTypeEntity(type);
        return jpaRepository.countByType(entityType);
    }

    @Override
    @Transactional
    public void delete(PlaceId placeId) {
        log.debug("Deleting place: {}", placeId);
        jpaRepository.deleteById(placeId.getValue());
    }
}
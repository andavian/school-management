package org.school.management.geography.domain.repository;

import org.school.management.geography.domain.model.Place;
import org.school.management.geography.domain.valueobject.PlaceId;
import org.school.management.geography.domain.valueobject.ProvinceId;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository {
    Place save(Place place);
    Optional<Place> findById(PlaceId placeId);
    List<Place> findByProvinceId(ProvinceId provinceId);
    Optional<Place> findByNameAndProvinceId(String name, ProvinceId provinceId);
}
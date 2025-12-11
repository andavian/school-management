package org.school.management.academic.domain.repository;

import org.school.management.academic.domain.model.Orientation;
import org.school.management.academic.domain.valueobject.ids.OrientationId;

import java.util.List;
import java.util.Optional;

public interface OrientationRepository {
    Orientation save(Orientation orientation);

    Optional<Orientation> findById(OrientationId orientationId);

    Optional<Orientation> findByCode(String code);

    List<Orientation> findAll();

    List<Orientation> findActiveOrientations();

    List<Orientation> findByAvailableFromYear(int yearLevel);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    long count();

    void delete(OrientationId id);
}

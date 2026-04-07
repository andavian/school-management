package org.school.management.resources.domain.repository;

import org.school.management.resources.domain.model.ResourceUnit;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.resources.domain.valueobject.UnitId;
import org.school.management.resources.domain.valueobject.UnitStatus;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de dominio para persistencia de unidades físicas.
 * Implementado por ResourceUnitRepositoryAdapter en infrastructure/persistence/adapter/
 */
public interface ResourceUnitRepository {

    Optional<ResourceUnit> findByUnitId(UnitId unitId);

    /**
     * Retorna unidades de un recurso filtradas por su estado operativo.
     * Fundamental para el cálculo de disponibilidad.
     */
    List<ResourceUnit> findByResourceIdAndStatus(ResourceId resourceId, UnitStatus status);

    List<ResourceUnit> findAllByResourceId(ResourceId resourceId);

    boolean existsByUnitCode(String unitCode);

    ResourceUnit save(ResourceUnit resourceUnit);
}
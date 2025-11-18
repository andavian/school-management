package org.school.management.geography.domain.repository;

import org.school.management.geography.domain.model.Place;
import org.school.management.geography.domain.model.PlaceWithHierarchy;
import org.school.management.geography.domain.valueobject.PlaceId;
import org.school.management.geography.domain.valueobject.PlaceType;
import org.school.management.geography.domain.valueobject.ProvinceId;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository {

    /**
     * Guardar o actualizar un lugar
     */
    Place save(Place place);

    /**
     * Buscar lugar por ID
     */
    Optional<Place> findById(PlaceId placeId);

    /**
     * Buscar lugar por nombre y provincia
     */
    Optional<Place> findByNameAndProvince(String name, ProvinceId provinceId);

    /**
     * Buscar lugar con su jerarquía completa (place + province + country)
     */
    Optional<PlaceWithHierarchy> findByIdWithHierarchy(PlaceId placeId);

    /**
     * Listar todos los lugares
     */
    List<Place> findAll();

    /**
     * Listar lugares de una provincia
     */
    List<Place> findByProvinceId(ProvinceId provinceId);

    /**
     * Listar lugares por tipo
     */
    List<Place> findByType(PlaceType type);

    /**
     * Listar lugares de una provincia por tipo
     */
    List<Place> findByProvinceIdAndType(ProvinceId provinceId, PlaceType type);

    /**
     * Buscar lugares por código postal
     */
    List<Place> findByPostalCode(String postalCode);

    /**
     * Buscar lugares por nombre (búsqueda parcial)
     */
    List<Place> searchByName(String namePattern);

    /**
     * Buscar lugares por nombre en una provincia específica
     */
    List<Place> searchByNameInProvince(String namePattern, ProvinceId provinceId);

    /**
     * Buscar lugares con jerarquía por nombre
     */
    List<PlaceWithHierarchy> searchByNameWithHierarchy(String namePattern);

    /**
     * Buscar lugares con jerarquía en una provincia
     */
    List<PlaceWithHierarchy> searchByNameInProvinceWithHierarchy(
            String namePattern,
            ProvinceId provinceId
    );

    /**
     * Verificar si existe un lugar con ese nombre en la provincia
     */
    boolean existsByNameAndProvince(String name, ProvinceId provinceId);

    /**
     * Contar lugares de una provincia
     */
    long countByProvince(ProvinceId provinceId);

    /**
     * Contar lugares por tipo
     */
    long countByType(PlaceType type);

    /**
     * Eliminar lugar
     */
    void delete(PlaceId placeId);
}

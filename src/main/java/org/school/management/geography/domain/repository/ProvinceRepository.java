package org.school.management.geography.domain.repository;

import org.school.management.geography.domain.model.Province;
import org.school.management.geography.domain.valueobject.CountryId;
import org.school.management.geography.domain.valueobject.ProvinceId;

import java.util.List;
import java.util.Optional;

public interface ProvinceRepository {

    /**
     * Guardar o actualizar una provincia
     */
    Province save(Province province);

    /**
     * Buscar provincia por ID
     */
    Optional<Province> findById(ProvinceId provinceId);

    /**
     * Buscar provincia por nombre y país
     */
    Optional<Province> findByNameAndCountry(String name, CountryId countryId);

    /**
     * Buscar provincia por código
     */
    Optional<Province> findByCode(String code);

    /**
     * Listar todas las provincias
     */
    List<Province> findAll();

    /**
     * Listar provincias de un país
     */
    List<Province> findByCountryId(CountryId countryId);

    /**
     * Buscar provincias por nombre (parcial)
     */
    List<Province> searchByName(String namePattern);

    /**
     * Verificar si existe una provincia con ese nombre en el país
     */
    boolean existsByNameAndCountry(String name, CountryId countryId);

    /**
     * Verificar si existe una provincia con ese código
     */
    boolean existsByCode(String code);

    /**
     * Contar provincias de un país
     */
    long countByCountry(CountryId countryId);

    /**
     * Eliminar provincia (solo si no tiene lugares asociados)
     */
    void delete(ProvinceId provinceId);
}

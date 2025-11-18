package org.school.management.geography.domain.repository;

import org.school.management.geography.domain.model.Country;
import org.school.management.geography.domain.valueobject.CountryId;
import org.school.management.geography.domain.valueobject.IsoCode;

import java.util.List;
import java.util.Optional;

public interface CountryRepository {

    /**
     * Guardar o actualizar un país
     */
    Country save(Country country);

    /**
     * Buscar país por ID
     */
    Optional<Country> findById(CountryId countryId);

    /**
     * Buscar país por código ISO
     */
    Optional<Country> findByIsoCode(IsoCode isoCode);

    /**
     * Buscar país por código ISO (String)
     */
    default Optional<Country> findByIsoCode(String isoCode) {
        return findByIsoCode(IsoCode.of(isoCode));
    }

    /**
     * Buscar país por nombre
     */
    Optional<Country> findByName(String name);

    /**
     * Listar todos los países
     */
    List<Country> findAll();

    /**
     * Verificar si existe un país con ese código ISO
     */
    boolean existsByIsoCode(IsoCode isoCode);

    /**
     * Verificar si existe un país con ese nombre
     */
    boolean existsByName(String name);

    /**
     * Contar total de países
     */
    long count();
}
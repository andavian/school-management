package org.school.management.geography.infra.persistence.repository;

import org.school.management.geography.infra.persistence.entity.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CountryJpaRepository extends JpaRepository<CountryEntity, UUID> {

    Optional<CountryEntity> findByIsoCode(String isoCode);

    Optional<CountryEntity> findByName(String name);

    boolean existsByIsoCode(String isoCode);

    boolean existsByName(String name);
}

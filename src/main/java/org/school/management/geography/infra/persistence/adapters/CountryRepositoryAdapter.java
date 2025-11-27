package org.school.management.geography.infra.persistence.adapters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.geography.domain.model.Country;
import org.school.management.geography.domain.repository.CountryRepository;
import org.school.management.geography.domain.valueobject.CountryId;
import org.school.management.geography.domain.valueobject.IsoCode;
import org.school.management.geography.infra.persistence.entity.CountryEntity;
import org.school.management.geography.infra.persistence.mappers.GeographyPersistenceMapper;
import org.school.management.geography.infra.persistence.repository.CountryJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CountryRepositoryAdapter implements CountryRepository {

    private final CountryJpaRepository jpaRepository;
    private final GeographyPersistenceMapper mapper;

    @Override
    @Transactional
    public Country save(Country country) {
        log.debug("Saving country: {}", country.getNameAsString());
        CountryEntity entity = mapper.toCountryEntity(country);
        CountryEntity saved = jpaRepository.save(entity);
        return mapper.toCountryDomain(saved);
    }

    @Override
    public Optional<Country> findById(CountryId countryId) {
        return jpaRepository.findById(countryId.getValue())
                .map(mapper::toCountryDomain);
    }

    @Override
    public Optional<Country> findByIsoCode(IsoCode isoCode) {
        return jpaRepository.findByIsoCode(isoCode.getValue())
                .map(mapper::toCountryDomain);
    }

    @Override
    public Optional<Country> findByName(String name) {
        return jpaRepository.findByName(name)
                .map(mapper::toCountryDomain);
    }

    @Override
    public List<Country> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toCountryDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByIsoCode(IsoCode isoCode) {
        return jpaRepository.existsByIsoCode(isoCode.getValue());
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}

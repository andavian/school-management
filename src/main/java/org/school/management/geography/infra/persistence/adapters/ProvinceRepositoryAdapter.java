package org.school.management.geography.infra.persistence.adapters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.geography.domain.model.Province;
import org.school.management.geography.domain.repository.ProvinceRepository;
import org.school.management.geography.domain.valueobject.CountryId;
import org.school.management.geography.domain.valueobject.ProvinceId;
import org.school.management.geography.infra.persistence.entity.ProvinceEntity;
import org.school.management.geography.infra.persistence.mappers.GeographyPersistenceMapper;
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
public class ProvinceRepositoryAdapter implements ProvinceRepository {

    private final ProvinceJpaRepository jpaRepository;
    private final GeographyPersistenceMapper mapper;

    @Override
    @Transactional
    public Province save(Province province) {
        log.debug("Saving province: {}", province.getNameAsString());
        ProvinceEntity entity = mapper.toProvinceEntity(province);
        ProvinceEntity saved = jpaRepository.save(entity);
        return mapper.toProvinceDomain(saved);
    }

    @Override
    public Optional<Province> findById(ProvinceId provinceId) {
        return jpaRepository.findById(provinceId.getValue())
                .map(mapper::toProvinceDomain);
    }

    @Override
    public Optional<Province> findByNameAndCountry(String name, CountryId countryId) {
        return jpaRepository.findByNameAndCountryId(name, countryId.getValue())
                .map(mapper::toProvinceDomain);
    }

    @Override
    public Optional<Province> findByCode(String code) {
        return jpaRepository.findByCode(code)
                .map(mapper::toProvinceDomain);
    }

    @Override
    public List<Province> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toProvinceDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Province> findByCountryId(CountryId countryId) {
        return jpaRepository.findByCountryId(countryId.getValue()).stream()
                .map(mapper::toProvinceDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Province> searchByName(String namePattern) {
        return jpaRepository.searchByName(namePattern).stream()
                .map(mapper::toProvinceDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByNameAndCountry(String name, CountryId countryId) {
        return jpaRepository.existsByNameAndCountryId(name, countryId.getValue());
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }

    @Override
    public long countByCountry(CountryId countryId) {
        return jpaRepository.countByCountryId(countryId.getValue());
    }

    @Override
    @Transactional
    public void delete(ProvinceId provinceId) {
        log.debug("Deleting province: {}", provinceId);
        jpaRepository.deleteById(provinceId.getValue());
    }
}

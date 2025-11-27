package org.school.management.geography.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.geography.application.dto.response.CountryResponse;
import org.school.management.geography.application.mappers.GeographyApplicationMapper;
import org.school.management.geography.domain.model.Country;
import org.school.management.geography.domain.repository.CountryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ListCountriesUseCase {

    private final CountryRepository countryRepository;
    private final GeographyApplicationMapper mapper;

    public List<CountryResponse> execute() {
        log.info("Listing all countries");

        List<Country> countries = countryRepository.findAll();

        return countries.stream()
                .map(mapper::toCountryResponse)
                .collect(Collectors.toList());
    }
}
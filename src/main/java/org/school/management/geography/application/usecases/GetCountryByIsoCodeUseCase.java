package org.school.management.geography.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.geography.application.dto.request.GetCountryByIsoCodeRequest;
import org.school.management.geography.application.dto.response.CountryResponse;
import org.school.management.geography.application.mappers.GeographyApplicationMapper;
import org.school.management.geography.domain.exception.CountryNotFoundException;
import org.school.management.geography.domain.model.Country;
import org.school.management.geography.domain.repository.CountryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetCountryByIsoCodeUseCase {

    private final CountryRepository countryRepository;
    private final GeographyApplicationMapper mapper;

    public CountryResponse execute(GetCountryByIsoCodeRequest request) {
        log.info("Getting country by ISO code: {}", request.isoCode());

        Country country = countryRepository.findByIsoCode(request.isoCode())
                .orElseThrow(() -> new CountryNotFoundException(request.isoCode()));

        return mapper.toCountryResponse(country);
    }
}
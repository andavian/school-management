package org.school.management.geography.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.geography.application.dto.request.ListProvincesByCountryRequest;
import org.school.management.geography.application.dto.response.ProvinceResponse;
import org.school.management.geography.application.mappers.GeographyApplicationMapper;
import org.school.management.geography.domain.exception.CountryNotFoundException;
import org.school.management.geography.domain.model.Province;
import org.school.management.geography.domain.repository.CountryRepository;
import org.school.management.geography.domain.repository.ProvinceRepository;
import org.school.management.geography.domain.valueobject.CountryId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ListProvincesByCountryUseCase {

    private final ProvinceRepository provinceRepository;
    private final CountryRepository countryRepository;
    private final GeographyApplicationMapper mapper;

    public List<ProvinceResponse> execute(ListProvincesByCountryRequest request) {
        log.info("Listing provinces for country: {}", request.countryId());

        CountryId countryId = CountryId.of(request.countryId());

        // Verificar que el país existe
        countryRepository.findById(countryId)
                .orElseThrow(() -> new CountryNotFoundException(countryId));

        List<Province> provinces = provinceRepository.findByCountryId(countryId);

        return provinces.stream()
                .map(mapper::toProvinceResponse)
                .collect(Collectors.toList());
    }
}

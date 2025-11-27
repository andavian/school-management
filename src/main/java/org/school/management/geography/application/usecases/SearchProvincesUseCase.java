package org.school.management.geography.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.geography.application.dto.request.SearchProvincesRequest;
import org.school.management.geography.application.dto.response.ProvinceResponse;
import org.school.management.geography.application.mappers.GeographyApplicationMapper;
import org.school.management.geography.domain.model.Province;
import org.school.management.geography.domain.repository.ProvinceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SearchProvincesUseCase {

    private final ProvinceRepository provinceRepository;
    private final GeographyApplicationMapper mapper;

    public List<ProvinceResponse> execute(SearchProvincesRequest request) {
        log.info("Searching provinces: {}", request.query());

        List<Province> provinces = provinceRepository.searchByName(request.query());

        return provinces.stream()
                .map(mapper::toProvinceResponse)
                .collect(Collectors.toList());
    }
}

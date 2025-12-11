package org.school.management.academic.application.usecases.year;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.response.AcademicYearResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.repository.AcademicYearRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ListAcademicYearsUseCase {

    private final AcademicYearRepository academicYearRepository;
    private final AcademicApplicationMapper mapper;

    public List<AcademicYearResponse> execute() {
        log.debug("Listing all academic years");

        return academicYearRepository.findAll().stream()
                .map(mapper::toAcademicYearResponse)
                .collect(Collectors.toList());
    }
}

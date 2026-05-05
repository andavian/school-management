package org.school.management.academic.application.usecases.qualification_registry;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.response.QualificationRegistryResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.model.QualificationRegistry;
import org.school.management.academic.domain.repository.QualificationRegistryRepository;
import org.school.management.academic.domain.valueobject.enums.RegistryStatus;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SearchQualificationRegistriesUseCase {

    private final QualificationRegistryRepository repository;
    private final AcademicApplicationMapper mapper;

    public List<QualificationRegistryResponse> execute(String academicYearId, String status) {
        log.debug("Searching qualification registries with filters — academicYearId: {}, status: {}", academicYearId, status);

        Optional<AcademicYearId> yearIdOpt = (academicYearId != null && !academicYearId.isBlank())
                ? Optional.of(AcademicYearId.from(academicYearId))
                : Optional.empty();

        Optional<RegistryStatus> statusOpt = (status != null && !status.isBlank())
                ? Optional.of(RegistryStatus.valueOf(status.toUpperCase()))
                : Optional.empty();

        List<QualificationRegistry> registries;

        if (yearIdOpt.isPresent()) {
            // Buscar por año y luego filtrar por estado si es necesario
            registries = repository.findByAcademicYear(yearIdOpt.get());
            if (statusOpt.isPresent()) {
                registries = registries.stream()
                        .filter(r -> r.getStatus() == statusOpt.get())
                        .collect(Collectors.toList());
            }
        } else if (statusOpt.isPresent()) {
            registries = repository.findByStatus(statusOpt.get());
        } else {
            registries = repository.findAll();
        }

        log.debug("Found {} registries", registries.size());
        return registries.stream()
                .map(mapper::toQualificationRegistryResponse)
                .collect(Collectors.toList());
    }
}
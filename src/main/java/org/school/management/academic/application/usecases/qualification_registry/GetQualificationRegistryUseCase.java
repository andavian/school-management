package org.school.management.academic.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.response.QualificationRegistryResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.model.QualificationRegistry;
import org.school.management.academic.domain.repository.QualificationRegistryRepository;
import org.school.management.academic.domain.valueobject.ids.RegistryId;
import org.school.management.academic.domain.exception.QualificationRegistryNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GetQualificationRegistryUseCase {

    private final QualificationRegistryRepository repository;
    private final AcademicApplicationMapper mapper;

    public QualificationRegistryResponse execute(String registryId) {
        log.debug("Fetching qualification registry: {}", registryId);

        RegistryId id = RegistryId.from(registryId);
        QualificationRegistry registry = repository.findById(id)
                .orElseThrow(() -> new QualificationRegistryNotFoundException("Registry not found: " + registryId));

        return mapper.toQualificationRegistryResponse(registry);
    }
}
package org.school.management.academic.application.usecases.qualification_registry;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.response.QualificationRegistryResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.model.QualificationRegistry;
import org.school.management.academic.domain.repository.QualificationRegistryRepository;
import org.school.management.academic.domain.valueobject.ids.RegistryId;
import org.school.management.academic.domain.exception.QualificationRegistryNotFoundException;
import org.school.management.academic.domain.exception.RegistryAlreadyClosedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CloseQualificationRegistryUseCase {

    private final QualificationRegistryRepository repository;
    private final AcademicApplicationMapper mapper;

    public QualificationRegistryResponse execute(String registryId) {
        log.info("Closing qualification registry: {}", registryId);

        RegistryId id = RegistryId.from(registryId);
        QualificationRegistry registry = repository.findById(id)
                .orElseThrow(() -> new QualificationRegistryNotFoundException("Registry not found: " + registryId));

        if (registry.getStatus() == org.school.management.academic.domain.valueobject.enums.RegistryStatus.CLOSED) {
            throw new RegistryAlreadyClosedException(registryId);
        }

        registry = registry.close();
        registry = repository.save(registry);

        log.info("Successfully closed registry: {}", registry.getRegistryNumberAsString());
        return mapper.toQualificationRegistryResponse(registry);
    }
}

package org.school.management.academic.application.usecases.qualification_registry;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.request.CreateQualificationRegistryRequest;
import org.school.management.academic.application.dto.response.QualificationRegistryResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.model.QualificationRegistry;
import org.school.management.academic.domain.repository.QualificationRegistryRepository;
import org.school.management.academic.domain.service.QualificationRegistryFactory;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CreateQualificationRegistryUseCase {

    private final QualificationRegistryFactory factory;
    private final QualificationRegistryRepository repository;
    private final AcademicApplicationMapper mapper;

    public QualificationRegistryResponse execute(CreateQualificationRegistryRequest request) {
        log.info("Creating qualification registry for academic year: {}", request.academicYearId());

        AcademicYearId academicYearId = AcademicYearId.from(request.academicYearId());
        QualificationRegistry registry = factory.create(academicYearId);
        registry = repository.save(registry);

        log.info("Created qualification registry: {}", registry.getRegistryNumberAsString());
        return mapper.toQualificationRegistryResponse(registry);
    }
}
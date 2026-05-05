package org.school.management.academic.application.usecases.qualification_registry;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.request.InitRegistrySequenceRequest;
import org.school.management.academic.application.dto.response.QualificationRegistryResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.model.QualificationRegistry;
import org.school.management.academic.domain.repository.QualificationRegistryRepository;
import org.school.management.academic.domain.service.QualificationRegistryFactory;
import org.school.management.academic.domain.service.RegistryNumberGenerator;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InitializeRegistrySequenceUseCase {

    private final QualificationRegistryRepository registryRepository;
    private final QualificationRegistryFactory factory;
    private final RegistryNumberGenerator numberGenerator;
    private final AcademicApplicationMapper mapper;

    public QualificationRegistryResponse execute(InitRegistrySequenceRequest request) {
        log.info("Initializing registry sequence with last paper number: {}", request.lastPaperNumber());

        // Solo se permite si no existe ningún registro en el sistema
        if (registryRepository.countAll() > 0) {
            throw new IllegalStateException("Cannot seed sequence: qualification registries already exist");
        }

        AcademicYearId academicYearId = AcademicYearId.from(request.academicYearId());
        int nextSequence = request.lastPaperNumber() + 1;
        String registryNumber = numberGenerator.formatNumber(request.lastPaperNumber(), nextSequence);

        QualificationRegistry registry = factory.createWithNumber(academicYearId, registryNumber);
        registry = registryRepository.save(registry);

        log.info("Successfully seeded registry sequence. First registry: {}", registry.getRegistryNumberAsString());
        return mapper.toQualificationRegistryResponse(registry);
    }
}
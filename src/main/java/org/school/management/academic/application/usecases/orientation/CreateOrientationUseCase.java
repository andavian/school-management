package org.school.management.academic.application.usecases.orientation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.request.CreateOrientationRequest;
import org.school.management.academic.application.dto.response.OrientationResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.OrientationAlreadyExistsException;
import org.school.management.academic.domain.model.Orientation;
import org.school.management.academic.domain.repository.OrientationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateOrientationUseCase {

    private final OrientationRepository orientationRepository;
    private final AcademicApplicationMapper mapper;

    public OrientationResponse execute(CreateOrientationRequest request) {
        log.info("Creating orientation: {}", request.name());

        // 1. Validar que no exista el código
        if (orientationRepository.existsByCode(request.code())) {
            throw new OrientationAlreadyExistsException(
                    "Orientation with code " + request.code() + " already exists"
            );
        }

        // 2. Crear la orientación
        Orientation orientation = Orientation.create(
                request.name(),
                request.code(),
                request.description(),
                request.availableFromYear()
        );

        // 3. Guardar
        Orientation saved = orientationRepository.save(orientation);

        log.info("Orientation created successfully: {}", saved.getName());
        return mapper.toOrientationResponse(saved);
    }
}

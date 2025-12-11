package org.school.management.academic.application.usecases.year;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.request.*;
import org.school.management.academic.application.dto.response.*;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.*;
import org.school.management.academic.domain.model.*;
import org.school.management.academic.domain.repository.*;
import org.school.management.academic.domain.valueobject.*;
import org.school.management.academic.domain.valueobject.enums.AcademicYearStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateAcademicYearUseCase {

    private final AcademicYearRepository academicYearRepository;
    private final AcademicApplicationMapper mapper;

    public AcademicYearResponse execute(CreateAcademicYearRequest request) {
        log.info("Creating academic year: {}", request.year());


        if (academicYearRepository.existsByYear(Year.of(request.year()).getValue())) {
            throw new AcademicYearAlreadyExistsException(
                    "Academic year " + request.year() + " already exists"
            );
        }

        if (academicYearRepository.existsByYear(Year.of(request.year()).getValue())) {
            throw new AcademicYearAlreadyExistsException(
                    "Academic year " + request.year() + " already exists"
            );
        }

        // --- LÓGICA AGREGADA DE REGLA DE NEGOCIO ---
        boolean isCurrent = request.isCurrent();

        // 1.5. Validar que si se intenta crear como activo, no haya otro activo.
        if (isCurrent && academicYearRepository.existsByStatus(AcademicYearStatus.ACTIVE)) {
            log.warn("Another academic year is already active. New year {} will be set to PENDING.", request.year());
            isCurrent = false;
        }
        // -------------------------------------------

        // 2. Crear el año académico
        AcademicYear academicYear = AcademicYear.create(
                request.year(),
                request.startDate(),
                request.endDate(),
                isCurrent
        );

        // 3. Guardar
        AcademicYear saved = academicYearRepository.save(academicYear);

        log.info("Academic year created successfully: {}", saved.getYearValue());
        return mapper.toAcademicYearResponse(saved);
    }
}




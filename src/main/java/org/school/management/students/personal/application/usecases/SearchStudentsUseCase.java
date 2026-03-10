package org.school.management.students.personal.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.shared.geography.domain.valueobject.PlaceId;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.school.management.students.personal.application.dto.response.StudentSummaryResponse;
import org.school.management.students.personal.application.mappers.StudentPersonalDataApplicationMapper;
import org.school.management.students.personal.domain.repository.StudentPersonalDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SearchStudentsUseCase {

    private final StudentPersonalDataRepository studentRepository;
    private final StudentPersonalDataApplicationMapper mapper;

    public List<StudentSummaryResponse> execute(String dni, String fullName, UUID residencePlaceId) {
        log.debug("Searching students — dni: {}, fullName: {}, residencePlaceId: {}",
                dni, fullName, residencePlaceId);

        // Búsqueda por DNI — retorna exactamente uno o vacío
        if (dni != null && !dni.isBlank()) {
            return studentRepository.findByDni(Dni.of(dni))
                    .map(mapper::toStudentSummaryResponse)
                    .map(List::of)
                    .orElse(List.of());
        }

        // Búsqueda por lugar de residencia
        if (residencePlaceId != null) {
            return studentRepository
                    .findByResidencePlaceId(PlaceId.of(residencePlaceId))
                    .stream()
                    .map(mapper::toStudentSummaryResponse)
                    .toList();
        }

        // Búsqueda por nombre — fullName como último criterio (más costoso)
        if (fullName != null && !fullName.isBlank()) {
            return studentRepository
                    .findByFullNameContaining(fullName.trim())
                    .stream()
                    .map(mapper::toStudentSummaryResponse)
                    .toList();
        }

        // Sin criterios — retorna todos (solo ADMIN debería poder hacer esto)
        log.warn("SearchStudentsUseCase called without criteria — returning all students");
        return studentRepository.findAll()
                .stream()
                .map(mapper::toStudentSummaryResponse)
                .toList();
    }
}
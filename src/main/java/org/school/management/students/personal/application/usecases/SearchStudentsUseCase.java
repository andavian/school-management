package org.school.management.students.personal.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.students.personal.domain.repository.StudentPersonalDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Buscar estudiantes por diferentes criterios
 * <p>
 * Nota: Este Use Case requiere que el repository implemente
 * métodos de búsqueda adicionales (findByFullNameContaining, etc)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SearchStudentsUseCase {

    private final StudentPersonalDataRepository studentRepository;
    private final StudentPersonalDataMapper mapper;

    /**
     * Ejecuta el caso de uso
     *
     * @param request Criterios de búsqueda
     * @return Lista de estudiantes que coinciden
     */
    @Transactional(readOnly = true)
    public List<StudentSummaryResponse> execute(SearchStudentRequest request) {
        log.debug("Searching students with criteria: {}", request);

        List<StudentSummaryResponse> results = new ArrayList<>();

        // Buscar por DNI (exacto)
        if (request.dni() != null && !request.dni().isBlank()) {
            var dni = mapper.mapDni(request.dni());
            studentRepository.findByDni(dni)
                    .ifPresent(student -> results.add(mapper.toSummaryResponse(student)));
        }

        // TODO: Implementar búsqueda por nombre completo
        // Requiere agregar método al repository:
        // List<StudentPersonalData> findByFullNameContaining(String name)

        // TODO: Implementar búsqueda por lugar de residencia
        // Requiere agregar método al repository:
        // List<StudentPersonalData> findByResidencePlaceId(PlaceId placeId)

        log.debug("Found {} students", results.size());

        return results;
    }
}

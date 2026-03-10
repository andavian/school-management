package org.school.management.students.personal.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.students.personal.application.dto.response.StudentResponse;
import org.school.management.students.personal.application.mappers.StudentPersonalDataMapper;
import org.school.management.students.personal.domain.exception.StudentNotFoundException;
import org.school.management.students.personal.domain.repository.StudentPersonalDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Obtener estudiante por DNI
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GetStudentByDniUseCase {

    private final StudentPersonalDataRepository studentRepository;
    private final StudentPersonalDataMapper mapper;

    /**
     * Ejecuta el caso de uso
     *
     * @param dni DNI del estudiante
     * @return StudentResponse con datos completos
     * @throws StudentNotFoundException si no existe
     */
    @Transactional(readOnly = true)
    public StudentResponse execute(String dni) {
        log.debug("Getting student by DNI: {}", dni);

        var dniVO = mapper.mapDni(dni);

        var student = studentRepository.findByDni(dniVO)
                .orElseThrow(() -> new StudentNotFoundException(
                        "Student not found with DNI: " + dni
                ));

        return mapper.toResponse(student);
    }
}

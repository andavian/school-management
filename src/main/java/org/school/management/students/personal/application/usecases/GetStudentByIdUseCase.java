package org.school.management.students.personal.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.students.personal.application.dto.response.StudentResponse;
import org.school.management.students.personal.application.mappers.StudentPersonalDataMapper;
import org.school.management.students.personal.domain.exception.StudentNotFoundException;
import org.school.management.students.personal.domain.repository.StudentPersonalDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use Case: Obtener estudiante por ID
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GetStudentByIdUseCase {

    private final StudentPersonalDataRepository studentRepository;
    private final StudentPersonalDataMapper mapper;

    /**
     * Ejecuta el caso de uso
     *
     * @param studentId ID del estudiante
     * @return StudentResponse con datos completos
     * @throws StudentNotFoundException si no existe
     */
    @Transactional(readOnly = true)
    public StudentResponse execute(UUID studentId) {
        log.debug("Getting student by ID: {}", studentId);

        var student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new StudentNotFoundException(
                        "Student not found with ID: " + studentId
                ));

        return mapper.toResponse(student);
    }
}




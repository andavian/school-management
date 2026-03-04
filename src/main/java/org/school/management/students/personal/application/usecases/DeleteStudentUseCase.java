package org.school.management.students.personal.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.students.personal.domain.exception.StudentNotFoundException;
import org.school.management.students.personal.domain.repository.StudentPersonalDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use Case: Eliminar estudiante
 * <p>
 * Nota: En producción, considerar soft delete en lugar de eliminación física
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteStudentUseCase {

    private final StudentPersonalDataRepository studentRepository;

    /**
     * Ejecuta el caso de uso
     *
     * @param studentId ID del estudiante a eliminar
     * @throws StudentNotFoundException si no existe
     */
    @Transactional
    public void execute(UUID studentId) {
        log.warn("Deleting student with ID: {}", studentId);

        // Validar que existe
        if (!studentRepository.findByStudentId(studentId).isPresent()) {
            throw new StudentNotFoundException(
                    "Student not found with ID: " + studentId
            );
        }

        // Eliminar
        studentRepository.deleteByStudentId(studentId);

        log.info("Student deleted successfully: {}", studentId);
    }
}

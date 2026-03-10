package org.school.management.students.personal.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.students.personal.application.dto.response.StudentResponse;
import org.school.management.students.personal.domain.exception.StudentNotFoundException;
import org.school.management.students.personal.domain.repository.StudentPersonalDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Actualizar datos personales del estudiante
 * <p>
 * Responsabilidad:
 * - Validar que el estudiante exista
 * - Actualizar solo campos editables (no DNI, CUIL, birthDate)
 * - Usar método de dominio updatePersonalData() para mutación controlada
 * - Persistir cambios
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateStudentUseCase {

    private final StudentPersonalDataRepository studentRepository;
    private final StudentPersonalDataMapper mapper;

    /**
     * Ejecuta el caso de uso
     *
     * @param request   Datos a actualizar
     * @param updatedBy Usuario que ejecuta la acción
     * @return StudentResponse con datos actualizados
     * @throws StudentNotFoundException si no existe
     */
    @Transactional
    public StudentResponse execute(UpdateStudentRequest request, UserId updatedBy) {
        log.info("Updating student with ID: {}", request.studentId());

        // 1. Buscar estudiante
        StudentPersonalData student = studentRepository.findByStudentId(request.studentId())
                .orElseThrow(() -> new StudentNotFoundException(
                        "Student not found with ID: " + request.studentId()
                ));

        // 2. Actualizar datos usando método de dominio
        student.updatePersonalData(
                mapper.mapFullName(request),
                mapper.mapPhoneNumber(request.phone()),
                mapper.mapEmail(request.email()),
                mapper.mapAddress(request),
                updatedBy
        );

        // 3. Persistir cambios
        StudentPersonalData updated = studentRepository.save(student);

        log.info("Student updated successfully: {}", request.studentId());

        // 4. Convertir a DTO
        return mapper.toResponse(updated);
    }
}

package org.school.management.students.personal.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.school.management.students.personal.application.dto.response.StudentResponse;
import org.school.management.students.personal.domain.exception.StudentNotFoundException;
import org.school.management.students.personal.domain.model.StudentPersonalData;
import org.school.management.students.personal.domain.repository.StudentPersonalDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GetStudentByDniUseCase {

    private final StudentPersonalDataRepository studentRepository;
    private final GetStudentByIdUseCase getStudentByIdUseCase;

    public StudentResponse execute(String dni) {
        log.debug("Fetching student with DNI: {}", dni);

        StudentPersonalData student = studentRepository
                .findByDni(Dni.of(dni))
                .orElseThrow(() -> StudentNotFoundException.byDni(dni));

        return getStudentByIdUseCase.buildResponse(student);
    }
}
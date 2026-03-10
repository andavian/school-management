package org.school.management.students.personal.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.shared.person.domain.valueobject.*;
import org.school.management.shared.geography.domain.valueobject.PlaceId;
import org.school.management.students.personal.application.dto.request.UpdateStudentRequest;
import org.school.management.students.personal.application.dto.response.StudentResponse;
import org.school.management.students.personal.domain.exception.StudentNotFoundException;
import org.school.management.students.personal.domain.model.StudentPersonalData;
import org.school.management.students.personal.domain.repository.StudentPersonalDataRepository;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UpdateStudentUseCase {

    private final StudentPersonalDataRepository studentRepository;
    private final GetStudentByIdUseCase getStudentByIdUseCase;

    public StudentResponse execute(UUID studentId, UpdateStudentRequest request) {
        log.debug("Updating student with id: {}", studentId);

        StudentPersonalData student = studentRepository
                .findByStudentId(StudentPersonalDataId.from(studentId))
                .orElseThrow(() -> StudentNotFoundException.byId(studentId));

        student.updatePersonalData(
                FullName.of(request.firstName(), request.lastName()),
                request.phone() != null && !request.phone().isBlank()
                        ? PhoneNumber.of(request.phone()) : null,
                request.email() != null && !request.email().isBlank()
                        ? Email.of(request.email()) : null,
                new Address(
                        request.addressStreet(),
                        request.addressNumber(),
                        request.addressFloor(),
                        request.addressApartment(),
                        PlaceId.of(request.residencePlaceId()),
                        request.postalCode()
                )
        );

        StudentPersonalData saved = studentRepository.save(student);

        log.info("Student updated successfully — id: {}", studentId);
        return getStudentByIdUseCase.buildResponse(saved);
    }
}
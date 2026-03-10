package org.school.management.students.personal.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.geography.application.dto.request.GetPlaceByIdRequest;
import org.school.management.geography.application.dto.response.PlaceResponse;
import org.school.management.geography.application.usecases.GetPlaceByIdUseCase;
import org.school.management.students.personal.application.dto.response.StudentResponse;
import org.school.management.students.personal.application.mappers.StudentPersonalDataApplicationMapper;
import org.school.management.students.personal.domain.exception.StudentNotFoundException;
import org.school.management.students.personal.domain.model.StudentPersonalData;
import org.school.management.students.personal.domain.repository.StudentPersonalDataRepository;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GetStudentByIdUseCase {

    private final StudentPersonalDataRepository studentRepository;
    private final StudentPersonalDataApplicationMapper mapper;
    private final GetPlaceByIdUseCase getPlaceByIdUseCase;

    public StudentResponse execute(UUID studentId) {
        log.debug("Fetching student with id: {}", studentId);

        StudentPersonalData student = studentRepository
                .findByStudentId(StudentPersonalDataId.from(studentId))
                .orElseThrow(() -> StudentNotFoundException.byId(studentId));

        return buildResponse(student);
    }

    /**
     * Package-private — reutilizado por GetStudentByDniUseCase y SearchStudentsUseCase
     * para no duplicar la lógica de resolución de lugares.
     */
    StudentResponse buildResponse(StudentPersonalData student) {
        StudentResponse.PlaceResponse birthPlace =
                resolvePlaceResponse(student.getBirthPlaceId().value());

        StudentResponse.PlaceResponse residencePlace =
                resolvePlaceResponse(student.getAddress().placeId().value());

        return mapper.toStudentResponse(student, birthPlace, residencePlace);
    }

    private StudentResponse.PlaceResponse resolvePlaceResponse(UUID placeId) {
        try {
            PlaceResponse place = getPlaceByIdUseCase.execute(
                    new GetPlaceByIdRequest(placeId)
            );
            return new StudentResponse.PlaceResponse(
                    place.placeId(),
                    place.name(),
                    place.provinceName(),
                    place.countryName()
            );
        } catch (Exception e) {
            log.warn("Could not resolve place with id: {}. Returning partial response.", placeId);
            return new StudentResponse.PlaceResponse(placeId, null, null, null);
        }
    }
}
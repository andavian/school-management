package org.school.management.teachers.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.geography.application.usecases.GetPlaceByIdUseCase;
import org.school.management.teachers.application.dto.response.TeacherResponse;
import org.school.management.teachers.application.mapper.TeacherApplicationMapper;
import org.school.management.teachers.domain.exception.TeacherNotFoundException;
import org.school.management.teachers.domain.model.Teacher;
import org.school.management.teachers.domain.repository.TeacherRepository;
import org.school.management.teachers.domain.valueobject.TeacherId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GetTeacherByIdUseCase {

    private final TeacherRepository teacherRepository;
    private final TeacherApplicationMapper mapper;
    private final GetPlaceByIdUseCase getPlaceByIdUseCase;

    public TeacherResponse execute(UUID teacherId) {
        log.debug("Getting teacher by id: {}", teacherId);

        Teacher teacher = teacherRepository
                .findByTeacherId(TeacherId.from(teacherId))
                .orElseThrow(() -> TeacherNotFoundException.byId(teacherId));

        return buildResponse(teacher);
    }

    /**
     * Package-private — reutilizable por otros use cases del mismo módulo
     * (mismo patrón que GetStudentByIdUseCase.buildResponse()).
     */
    TeacherResponse buildResponse(Teacher teacher) {
        TeacherResponse.PlaceResponse birthPlaceResponse = resolvePlaceResponse(
                teacher.getBirthPlaceId() != null ? teacher.getBirthPlaceId().value() : null
        );
        TeacherResponse.PlaceResponse residencePlaceResponse = resolvePlaceResponse(
                teacher.getAddress() != null ? teacher.getAddress().placeId().value() : null
        );

        return mapper.toTeacherResponse(teacher, birthPlaceResponse, residencePlaceResponse);
    }

    private TeacherResponse.PlaceResponse resolvePlaceResponse(UUID placeId) {
        if (placeId == null) return null;
        try {
            var placeResponse = getPlaceByIdUseCase.execute(
                    new org.school.management.geography.application.dto.request.GetPlaceByIdRequest(placeId)
            );
            return new TeacherResponse.PlaceResponse(
                    placeResponse.placeId(),
                    placeResponse.name(),
                    placeResponse.provinceName(),
                    placeResponse.countryName()
            );
        } catch (Exception e) {
            log.warn("Could not resolve place with id: {}", placeId);
            return null;
        }
    }
}
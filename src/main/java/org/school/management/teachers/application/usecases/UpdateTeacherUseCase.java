package org.school.management.teachers.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.shared.geography.domain.valueobject.PlaceId;
import org.school.management.shared.person.domain.valueobject.*;
import org.school.management.teachers.application.dto.request.CreateTeacherRequest;
import org.school.management.teachers.application.dto.request.UpdateTeacherRequest;
import org.school.management.teachers.application.dto.response.TeacherResponse;
import org.school.management.teachers.domain.exception.TeacherNotFoundException;
import org.school.management.teachers.domain.model.Teacher;
import org.school.management.teachers.domain.repository.TeacherRepository;
import org.school.management.teachers.domain.valueobject.TeacherId;
import org.school.management.teachers.domain.valueobject.TeacherSpecialization;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateTeacherUseCase {

    private final TeacherRepository teacherRepository;
    private final GetTeacherByIdUseCase getTeacherByIdUseCase;

    @Transactional
    public TeacherResponse execute(UUID teacherId, UpdateTeacherRequest request) {
        log.info("Updating teacher: {}", teacherId);

        Teacher teacher = teacherRepository
                .findByTeacherId(TeacherId.from(teacherId))
                .orElseThrow(() -> TeacherNotFoundException.byId(teacherId));

        // Sección personal — solo actualiza si el campo viene no-null
        if (hasPersonalChanges(request)) {
            FullName fullName = request.firstName() != null || request.lastName() != null
                    ? FullName.of(
                    request.firstName() != null
                            ? request.firstName() : teacher.getFullName().firstName(),
                    request.lastName() != null
                            ? request.lastName() : teacher.getFullName().lastName())
                    : teacher.getFullName();

            PlaceId birthPlaceId = request.birthPlaceId() != null
                    ? PlaceId.of(UUID.fromString(request.birthPlaceId()))
                    : teacher.getBirthPlaceId();

            Gender gender = request.gender() != null
                    ? Gender.valueOf(request.gender())
                    : teacher.getGender();

            Nationality nationality = request.nationality() != null
                    ? Nationality.of(request.nationality())
                    : teacher.getNationality();

            teacher.updatePersonalInfo(fullName, request.birthDate() != null
                            ? request.birthDate() : teacher.getBirthDate(),
                    birthPlaceId, gender, nationality);
        }

        // Sección contacto
        if (hasContactChanges(request)) {
            PhoneNumber phone = request.phone() != null
                    ? PhoneNumber.of(request.phone())
                    : teacher.getPhone();

            Address address = request.address() != null
                    ? buildAddress(request.address(), teacher)
                    : teacher.getAddress();

            teacher.updateContactInfo(phone, address);
        }

        // Sección profesional
        if (hasProfessionalChanges(request)) {
            TeacherSpecialization specialization = request.specialization() != null
                    ? TeacherSpecialization.of(request.specialization())
                    : teacher.getSpecialization();

            teacher.updateProfessionalInfo(
                    specialization,
                    request.teachingLicense() != null
                            ? request.teachingLicense() : teacher.getTeachingLicense(),
                    request.employmentType() != null
                            ? request.employmentType() : teacher.getEmploymentType()
            );
        }

        // Estado de empleo (ACTIVE → INACTIVE / RETIRED)
        if (request.employmentStatus() != null) {
            switch (request.employmentStatus()) {
                case RETIRED  -> teacher.retire();
                case INACTIVE -> teacher.deactivate();
                case ACTIVE   -> {} // reactivación futura — no implementada aún
            }
        }

        Teacher updatedTeacher = teacherRepository.save(teacher);

        log.info("Teacher updated successfully: {}", teacherId);

        return getTeacherByIdUseCase.buildResponse(updatedTeacher);
    }

    private boolean hasPersonalChanges(UpdateTeacherRequest r) {
        return r.firstName() != null || r.lastName() != null || r.birthDate() != null
                || r.birthPlaceId() != null || r.gender() != null || r.nationality() != null;
    }

    private boolean hasContactChanges(UpdateTeacherRequest r) {
        return r.phone() != null || r.address() != null;
    }

    private boolean hasProfessionalChanges(UpdateTeacherRequest r) {
        return r.specialization() != null || r.teachingLicense() != null
                || r.employmentType() != null;
    }

    private Address buildAddress(CreateTeacherRequest.AddressRequest req, Teacher existing) {
        if (req.street() == null && req.placeId() == null) return existing.getAddress();

        PlaceId placeId = req.placeId() != null
                ? PlaceId.of(UUID.fromString(req.placeId()))
                : (existing.getAddress() != null ? existing.getAddress().placeId() : null);

        String street = req.street() != null
                ? req.street()
                : (existing.getAddress() != null ? existing.getAddress().street() : null);

        return new Address(
                street,
                req.number(),
                req.floor(),
                req.apartment(),
                placeId,
                req.postalCode()
        );
    }
}

package org.school.management.teachers.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.domain.service.EmailService;
import org.school.management.shared.geography.domain.valueobject.PlaceId;
import org.school.management.shared.person.domain.valueobject.*;
import org.school.management.teachers.application.dto.request.CreateTeacherRequest;
import org.school.management.teachers.application.dto.response.TeacherResponse;
import org.school.management.teachers.domain.exception.TeacherAlreadyExistsException;
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
public class CreateTeacherUseCase {

    private final TeacherRepository teacherRepository;
    // Nombre completamente calificado — evita colisión con esta clase
    private final org.school.management.auth.application.usecases.admin.CreateTeacherUseCase authCreateTeacherUseCase;
    private final GetTeacherByIdUseCase getTeacherByIdUseCase;
    private final EmailService emailService;

    @Transactional
    public TeacherResponse execute(CreateTeacherRequest request, UUID createdByUserId) {
        log.info("Creating teacher: {} {} - DNI: {}",
                request.firstName(), request.lastName(), request.dni());

        // 1. Validar unicidad DNI
        Dni dni = Dni.of(request.dni());
        if (teacherRepository.existsByDni(dni)) {
            log.warn("Teacher already exists with DNI: {}", request.dni());
            throw TeacherAlreadyExistsException.withDni(request.dni());
        }

        // 2. Validar unicidad CUIL
        if (teacherRepository.existsByCuil(request.cuil())) {
            log.warn("Teacher already exists with CUIL: {}", request.cuil());
            throw TeacherAlreadyExistsException.withCuil(request.cuil());
        }

        // 3. Crear User con rol TEACHER via auth/ (genera password + token de activación)
        var authResponse = authCreateTeacherUseCase.execute(
                new org.school.management.auth.application.dto.requests.CreateTeacherRequest(
                        request.dni(),
                        request.firstName(),
                        request.lastName(),
                        request.email(),
                        request.phone(),
                        request.specialization() != null ? request.specialization() : ""
                )
        );
        UserId userId = UserId.from(UUID.fromString(authResponse.userId()));

        // 4. Construir VOs del dominio
        FullName fullName       = FullName.of(request.firstName(), request.lastName());
        Cuil cuil               = Cuil.of(request.cuil());
        Email email             = Email.of(request.email());
        PhoneNumber phone       = PhoneNumber.of(request.phone());
        Gender gender           = request.gender() != null
                ? Gender.valueOf(request.gender()) : null;
        Nationality nationality = request.nationality() != null
                ? Nationality.of(request.nationality()) : Nationality.of("Argentina");
        PlaceId birthPlaceId    = request.birthPlaceId() != null
                ? PlaceId.of(UUID.fromString(request.birthPlaceId())) : null;
        Address address         = buildAddress(request.address());
        TeacherSpecialization specialization = TeacherSpecialization.of(request.specialization());

        // 5. Crear entidad de dominio Teacher
        Teacher teacher = Teacher.create(
                TeacherId.generate(),
                userId,
                fullName,
                dni,
                cuil,
                email,
                request.birthDate(),
                birthPlaceId,
                gender,
                nationality,
                phone,
                address,
                specialization,
                request.teachingLicense(),
                request.hireDate(),
                request.employmentType(),
                UserId.from(createdByUserId)
        );

        // 6. Persistir
        Teacher savedTeacher = teacherRepository.save(teacher);

        emailService.sendTeacherInvitation(
                request.email(),
                request.firstName(),
                request.lastName(),
                request.dni(),
                authResponse.temporaryPassword(),
                ""      // activationLink vacío — pendiente cuando se agregue confirmationToken a authResponse
        );

        log.info("Teacher created successfully. DNI: {} - ID: {}",
                request.dni(), savedTeacher.getTeacherId().asString());

        return getTeacherByIdUseCase.buildResponse(savedTeacher);
    }

    private Address buildAddress(CreateTeacherRequest.AddressRequest req) {
        if (req == null || req.street() == null || req.placeId() == null) return null;
        return new Address(
                req.street(),
                req.number(),
                req.floor(),
                req.apartment(),
                PlaceId.of(UUID.fromString(req.placeId())),
                req.postalCode()
        );
    }
}

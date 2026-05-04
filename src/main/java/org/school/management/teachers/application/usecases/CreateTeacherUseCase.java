package org.school.management.teachers.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.dto.requests.CreateUserRequest;
import org.school.management.auth.application.usecases.CreateUserUseCase;
import org.school.management.auth.application.usecases.GenerateConfirmationTokenUseCase;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.auth.infra.security.token.TokenHasher;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Caso de uso: crear un profesor completo.
 *
 * <p>Orquesta todos los pasos necesarios en orden:</p>
 * <ol>
 *   <li>Validar unicidad DNI y CUIL en {@code teachers/}</li>
 *   <li>Generar password temporal segura</li>
 *   <li>Crear {@code User} con rol TEACHER via {@link CreateUserUseCase} (inactivo)</li>
 *   <li>Generar token de confirmación opaco via {@link GenerateConfirmationTokenUseCase}</li>
 *   <li>Construir y persistir la entidad {@code Teacher}</li>
 *   <li>Asignar el <strong>hash</strong> del token al {@code Teacher} — nunca el raw token</li>
 *   <li>Enviar email de invitación con el link de activación (async)</li>
 * </ol>
 *
 * <p><strong>Seguridad:</strong> {@code Teacher.activationToken} almacena el hash SHA-256
 * del token de confirmación — no el token raw. El token raw viaja únicamente por email
 * y nunca se persiste en texto plano en ninguna tabla.</p>
 *
 * <p>El email se envía de forma asíncrona y falla silenciosamente — un fallo
 * de email nunca revierte la creación del profesor.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreateTeacherUseCase {

    private final TeacherRepository teacherRepository;
    private final CreateUserUseCase createUserUseCase;
    private final GenerateConfirmationTokenUseCase generateConfirmationTokenUseCase;
    private final GetTeacherByIdUseCase getTeacherByIdUseCase;
    private final EmailService emailService;
    private final TokenHasher tokenHasher;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Transactional
    public TeacherResponse execute(CreateTeacherRequest request, UUID createdByUserId) {
        log.info("Creating teacher — DNI: {}", request.dni());

        // 1. Validar unicidad DNI y CUIL en teachers/
        Dni dni = Dni.of(request.dni());
        if (teacherRepository.existsByDni(dni)) {
            throw TeacherAlreadyExistsException.withDni(request.dni());
        }
        if (teacherRepository.existsByCuil(request.cuil())) {
            throw TeacherAlreadyExistsException.withCuil(request.cuil());
        }

        // 2. Generar password temporal segura
        String plainPassword = generateSecurePassword();

        // 3. Crear User con rol TEACHER — inactivo hasta que active via link
        var userResponse = createUserUseCase.execute(
                CreateUserRequest.inactive(request.dni(), plainPassword, "ROLE_TEACHER")
        );
        UserId userId = UserId.from(userResponse.userId());

        // 4. Generar token de confirmación opaco (raw) via use case de auth/
        //    El use case persiste el hash en confirmation_codes y retorna el raw token
        String rawConfirmationToken = generateConfirmationTokenUseCase.execute(request.dni());

        // 5. Construir link de activación con el raw token — viaja SOLO por email
        String activationLink = frontendUrl + "/activate-account?token=" + rawConfirmationToken;

        // 6. Hashear el token para almacenarlo en Teacher
        //    Teacher.activationToken almacena el hash, no el raw token
        String tokenHash = tokenHasher.hash(rawConfirmationToken);

        // 7. Construir entidad Teacher
        Teacher teacher = Teacher.create(
                TeacherId.generate(),
                userId,
                FullName.of(request.firstName(), request.lastName()),
                dni,
                Cuil.of(request.cuil()),
                Email.of(request.email()),
                request.birthDate(),
                request.birthPlaceId() != null
                        ? PlaceId.of(UUID.fromString(request.birthPlaceId())) : null,
                request.gender() != null ? Gender.valueOf(request.gender()) : null,
                request.nationality() != null
                        ? Nationality.of(request.nationality()) : Nationality.of("Argentina"),
                PhoneNumber.of(request.phone()),
                buildAddress(request.address()),
                TeacherSpecialization.of(request.specialization()),
                request.teachingLicense(),
                request.hireDate(),
                request.employmentType(),
                UserId.from(createdByUserId)
        );

        // 8. Asignar el HASH del token — nunca el raw token
        teacher.assignActivationToken(tokenHash);

        Teacher saved = teacherRepository.save(teacher);

        // 9. Enviar email de invitación con el raw token en el link (async — falla silenciosamente)
        sendInvitationEmail(request, plainPassword, activationLink);

        log.info("Teacher created successfully — DNI: {}, pendingActivation: {}",
                request.dni(), saved.isPendingActivation());

        return getTeacherByIdUseCase.buildResponse(saved);
    }

    // ── helpers privados ──────────────────────────────────────────────────

    private String generateSecurePassword() {
        SecureRandom random = new SecureRandom();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&*";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    private void sendInvitationEmail(CreateTeacherRequest request,
                                     String plainPassword,
                                     String activationLink) {
        try {
            emailService.sendTeacherInvitation(
                    request.email(),
                    request.firstName(),
                    request.lastName(),
                    request.dni(),
                    plainPassword,
                    activationLink
            );
            log.info("Invitation email sent — DNI: {}", request.dni());
        } catch (Exception e) {
            log.error("Could not send invitation email to: {} — {}",
                    request.email(), e.getMessage());
        }
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
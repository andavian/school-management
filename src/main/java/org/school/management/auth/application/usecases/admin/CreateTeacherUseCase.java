package org.school.management.auth.application.usecases.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.dto.requests.CreateTeacherRequest;
import org.school.management.auth.application.dto.responses.CreateTeacherResponse;
import org.school.management.auth.application.mappers.AuthApplicationMapper;
import org.school.management.auth.domain.exception.DniAlreadyExistsException;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.PlainPassword;
import org.school.management.auth.infra.security.JwtTokenProvider;
import org.school.management.auth.infra.security.UserPrincipal;
import org.school.management.shared.domain.service.EmailService;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service("authCreateTeacherUseCase")
@RequiredArgsConstructor
@Slf4j
public class CreateTeacherUseCase {

    private final UserRepository userRepository;
    private final org.school.management.auth.domain.repository.RoleRepository roleRepository;
    private final AuthApplicationMapper mapper;
    private final HashedPassword.PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Transactional
    public CreateTeacherResponse execute(CreateTeacherRequest request) {
        log.info("Creando profesor: {} {} - DNI: {}",
                request.firstName(), request.lastName(), request.dni());

        // Validar DNI único
        Dni dni = mapper.toDni(request.dni());
        if (userRepository.existsByDni(dni)) {
            log.warn("Intento de crear profesor con DNI existente: {}", request.dni());
            throw new DniAlreadyExistsException("Ya existe un usuario con este DNI: " + request.dni());
        }

        // Generar password temporal seguro
        PlainPassword temporaryPassword = generateSecureTemporaryPassword();

        // Crear usuario profesor — inicia con active=false (requiere activación)
        User teacher = mapper.createTeacherFromRequest(
                request, temporaryPassword, passwordEncoder, roleRepository);

        User savedTeacher = userRepository.save(teacher);

        // Generar token de confirmación JWT (48h según config)
        UserDetails userPrincipal = new UserPrincipal(savedTeacher);
        String confirmationToken = jwtTokenProvider.generateConfirmationToken(userPrincipal);

        // Construir link de activación
        String activationLink = buildActivationLink(confirmationToken);

        // Enviar email de invitación (async — no bloquea ni revierte la transacción)
        boolean invitationSent = sendTeacherInvitation(
                request, savedTeacher, temporaryPassword.value(), activationLink);

        log.info("Profesor creado exitosamente. DNI: {} - Email: {} - ID: {}",
                request.dni(), request.email(), savedTeacher.getUserId().asString());

        return new CreateTeacherResponse(
                savedTeacher.getUserId().asString(),
                savedTeacher.getDni().value(),
                temporaryPassword.value(),
                invitationSent,
                confirmationToken          // ← propagado al orquestador teachers/
        );
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private String buildActivationLink(String confirmationToken) {
        return frontendUrl + "/activate-account?token=" + confirmationToken;
    }

    private boolean sendTeacherInvitation(CreateTeacherRequest request,
                                          User savedTeacher,
                                          String temporaryPassword,
                                          String activationLink) {
        try {
            emailService.sendTeacherInvitation(
                    request.email(),
                    request.firstName(),
                    request.lastName(),
                    savedTeacher.getDni().value(),
                    temporaryPassword,
                    activationLink
            );
            log.info("Email de invitación enviado a: {}", request.email());
            return true;
        } catch (Exception e) {
            // El email falla silenciosamente — no revierte la creación del usuario
            log.error("No se pudo enviar email de invitación a: {} — {}",
                    request.email(), e.getMessage());
            return false;
        }
    }

    private PlainPassword generateSecureTemporaryPassword() {
        SecureRandom random = new SecureRandom();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&*";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return PlainPassword.of(password.toString());
    }
}
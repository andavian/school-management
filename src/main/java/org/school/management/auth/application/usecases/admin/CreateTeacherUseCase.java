package org.school.management.auth.application.usecases.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.dto.requests.CreateTeacherRequest;
import org.school.management.auth.application.dto.responses.CreateTeacherResponse;
import org.school.management.auth.application.mappers.AuthApplicationMapper;
import org.school.management.auth.domain.exception.DniAlreadyExistsException;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.RoleRepository;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.PlainPassword;
import org.school.management.auth.infra.security.JwtTokenProvider;
import org.school.management.shared.person.domain.valueobject.DNI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateTeacherUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthApplicationMapper mapper;
    private final HashedPassword.PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public CreateTeacherResponse execute(CreateTeacherRequest request) {
        log.info("Creando profesor: {} {} - DNI: {}",
                request.firstName(), request.lastName(), request.dni());

        // Validar DNI único
        DNI dni = mapper.toDni(request.dni());

        if (userRepository.existsByDni(dni)) {
            log.warn("Intento de crear profesor con DNI existente: {}", request.dni());
            throw new DniAlreadyExistsException("Ya existe un usuario con este DNI: " + request.dni());
        }

        // Generar password temporal seguro
        PlainPassword temporaryPassword = generateSecureTemporaryPassword();

        // Crear usuario profesor usando factory method del mapper
        User teacher = mapper.createTeacherFromRequest(request, temporaryPassword, passwordEncoder, roleRepository);

        // Guardar
        User savedTeacher = userRepository.save(teacher);

        // Generar token de confirmación
        String confirmationToken = jwtTokenProvider.generateConfirmationToken(savedTeacher);

        // Enviar email de invitación
        boolean invitationSent = sendTeacherInvitation(savedTeacher, confirmationToken);

        log.info("Profesor creado exitosamente. DNI: {} - Email: {} - ID: {}",
                request.dni(), request.email(), savedTeacher.getUserId().asString());

        return new CreateTeacherResponse(
                savedTeacher.getUserId().asString(),
                savedTeacher.getDni().getValue(),
                temporaryPassword.getValue(),
                invitationSent
        );
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

    private boolean sendTeacherInvitation(User teacher, String confirmationToken) {
        log.info("=== EMAIL DE INVITACIÓN ===");
        log.info("DNI: {}", teacher.getDni().getValue());
        log.info("Token: {}", confirmationToken);
        log.info("Link: http://localhost:3000/activate-account?token={}", confirmationToken);
        log.info("==========================");
        return true;
    }


}
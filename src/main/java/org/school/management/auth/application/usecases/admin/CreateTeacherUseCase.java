package org.school.management.auth.application.usecases.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.dto.requests.CreateTeacherRequest;
import org.school.management.auth.application.dto.responses.CreateTeacherResponse;
import org.school.management.auth.application.mappers.AuthApplicationMapper;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.*;
import org.school.management.shared.domain.valueobjects.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateTeacherUseCase {

    private final UserRepository userRepository;
    private final AuthApplicationMapper mapper;
    private final HashedPassword.PasswordEncoder passwordEncoder;
    private final EmailService emailService; // Para enviar invitación

    @Transactional
    public CreateTeacherResponse execute(CreateTeacherRequest request) {
        Email email = Email.of(request.getEmail());

        // Verificar que no exista
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Ya existe un usuario con este email: " + request.getEmail());
        }

        // Generar password temporal
        PlainPassword temporaryPassword = generateTemporaryPassword();

        // Crear usuario TEACHER inactivo
        User teacher = User.create(
                email,
                temporaryPassword,
                Set.of(RoleName.teacher()),
                passwordEncoder
        );

        // Desactivar hasta que confirme email
        teacher.deactivate();

        User savedTeacher = userRepository.save(teacher);

        // Enviar email de invitación
        sendTeacherInvitation(savedTeacher, temporaryPassword);

        log.info("Profesor creado exitosamente: {}", email.getValue());

        return CreateTeacherResponse.builder()
                .userId(savedTeacher.getUserId().asString())
                .email(savedTeacher.getEmail().getValue())
                .temporaryPassword(temporaryPassword.getValue()) // Solo para testing/demo
                .invitationSent(true)
                .build();
    }

    private PlainPassword generateTemporaryPassword() {
        // Generar password seguro temporal
        String tempPassword = "TempPass123!"; // En producción usar generador seguro
        return PlainPassword.of(tempPassword);
    }

    private void sendTeacherInvitation(User teacher, PlainPassword temporaryPassword) {
        // Lógica para enviar email (implementar después)
        log.info("Enviando invitación a: {}", teacher.getEmail().getValue());
    }

    public static class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }
}

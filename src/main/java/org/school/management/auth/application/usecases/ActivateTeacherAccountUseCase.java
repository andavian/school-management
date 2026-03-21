package org.school.management.auth.application.usecases;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.dto.requests.ActivateAccountRequest;
import org.school.management.auth.application.dto.responses.ActivateAccountResponse;
import org.school.management.auth.domain.exception.InvalidOperationException;
import org.school.management.auth.domain.exception.InvalidTokenException;
import org.school.management.auth.domain.exception.UserNotFoundException;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.PlainPassword;
import org.school.management.auth.domain.valueobject.RoleName;
import org.school.management.auth.infra.security.JwtTokenProvider;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.school.management.teachers.domain.exception.TeacherNotFoundException;
import org.school.management.teachers.domain.model.Teacher;
import org.school.management.teachers.domain.repository.TeacherRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivateTeacherAccountUseCase {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final HashedPassword.PasswordEncoder passwordEncoder;
    private final TeacherRepository teacherRepository;   // ← nuevo

    @Transactional
    public ActivateAccountResponse execute(ActivateAccountRequest request) {

        // 1. Validar token de confirmación
        if (!jwtTokenProvider.isConfirmationTokenValid(request.token())) {
            throw new InvalidTokenException("Token de activación inválido o expirado");
        }

        // 2. Obtener DNI del token y buscar el User
        String dni = jwtTokenProvider.getUsernameFromToken(request.token());
        User user = userRepository.findByDni(Dni.of(dni))
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        // 3. Verificar que sea profesor
        if (!user.hasRole(RoleName.teacher())) {
            throw new InvalidOperationException(
                    "Solo los profesores pueden usar este enlace de activación");
        }

        // 4. Cambiar password y activar el User
        PlainPassword newPassword = PlainPassword.of(request.newPassword());
        user.resetPassword(newPassword, passwordEncoder);
        user.activate();
        userRepository.save(user);

        // 5. Activar también el Teacher (misma transacción)
        Teacher teacher = teacherRepository.findByDni(Dni.of(dni))
                .orElseThrow(() -> TeacherNotFoundException.byDni(dni));

        teacher.activate(LocalDateTime.now());   // limpia activationToken + setea activatedAt
        teacherRepository.save(teacher);

        log.info("Cuenta de profesor activada exitosamente: DNI={}", dni);

        return new ActivateAccountResponse(true, "Cuenta activada exitosamente");
    }
}
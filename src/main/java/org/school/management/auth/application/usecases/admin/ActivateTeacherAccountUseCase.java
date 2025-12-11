package org.school.management.auth.application.usecases.admin;

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
import org.school.management.shared.person.domain.valueobject.DNI;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivateTeacherAccountUseCase {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final HashedPassword.PasswordEncoder passwordEncoder;

    @Transactional
    public ActivateAccountResponse execute(ActivateAccountRequest request) {
        // Validar token de confirmación
        if (!jwtTokenProvider.isConfirmationTokenValid(request.token())) {
            throw new InvalidTokenException("Token de activación inválido o expirado");
        }

        // Obtener usuario del token
        String dni = jwtTokenProvider.getUsernameFromToken(request.token());
        User user = userRepository.findByDni(DNI.of(dni))
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        // Verificar que sea profesor
        if (!user.hasRole(RoleName.teacher())) {
            throw new InvalidOperationException("Solo los profesores pueden usar este enlace de activación");
        }

        // Cambiar password
        PlainPassword newPassword = PlainPassword.of(request.newPassword());
        user.resetPassword(newPassword, passwordEncoder);

        // Activar cuenta
        user.activate();

        userRepository.save(user);

        log.info("Cuenta de profesor activada: {}", dni);

        return new ActivateAccountResponse(true, "Cuenta activada exitosamente" );

    }


}


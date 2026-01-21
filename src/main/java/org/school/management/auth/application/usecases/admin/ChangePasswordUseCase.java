package org.school.management.auth.application.usecases.admin;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.dto.requests.ChangePasswordRequest;
import org.school.management.auth.application.dto.responses.ChangePasswordResponse;
import org.school.management.auth.application.mappers.AuthApplicationMapper;
import org.school.management.auth.application.usecases.BlacklistTokenUseCase;
import org.school.management.auth.domain.exception.UserNotFoundException;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.PlainPassword;
import org.school.management.auth.domain.valueobject.UserId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final AuthApplicationMapper mapper;
    private final HashedPassword.PasswordEncoder passwordEncoder;
    private final BlacklistTokenUseCase blacklistTokenUseCase; // Invalidar tokens existentes

    @Transactional
    public ChangePasswordResponse execute(ChangePasswordRequest request) {
        UserId userId = mapper.toUserId(request.userId());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        PlainPassword currentPassword = mapper.toPlainPassword(request.currentPassword());
        PlainPassword newPassword = mapper.toPlainPassword(request.newPassword());

        // Cambiar password (valida el actual internamente)
        user.changePassword(currentPassword, newPassword, passwordEncoder);

        userRepository.save(user);

        // Invalidar todos los tokens existentes del usuario
        // blacklistAllUserTokens(user); // Implementar si es necesario

        log.info("Password cambiado para usuario: {}", user.getDni().value());

        return new ChangePasswordResponse(true,"Contraseña cambiada exitosamente" );

    }
}

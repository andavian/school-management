package org.school.management.auth.application.usecases.admin;

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
        if (!jwtTokenProvider.isConfirmationTokenValid(request.getToken())) {
            throw new InvalidTokenException("Token de activación inválido o expirado");
        }

        // Obtener usuario del token
        String email = jwtTokenProvider.getUsernameFromToken(request.getToken());
        User user = userRepository.findByEmail(Email.of(email))
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        // Verificar que sea profesor
        if (!user.isTeacher()) {
            throw new InvalidOperationException("Solo los profesores pueden usar este enlace de activación");
        }

        // Cambiar password
        PlainPassword newPassword = PlainPassword.of(request.getNewPassword());
        user.resetPassword(newPassword, passwordEncoder);

        // Activar cuenta
        user.activate();

        userRepository.save(user);

        log.info("Cuenta de profesor activada: {}", email);

        return ActivateAccountResponse.builder()
                .success(true)
                .message("Cuenta activada exitosamente")
                .build();
    }

    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException(String message) {
            super(message);
        }
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidOperationException extends RuntimeException {
        public InvalidOperationException(String message) {
            super(message);
        }
    }
}


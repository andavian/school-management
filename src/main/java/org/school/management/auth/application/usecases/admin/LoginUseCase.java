package org.school.management.auth.application.usecases.admin;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginUseCase {

    private final UserRepository userRepository;
    private final AuthApplicationMapper mapper;
    private final HashedPassword.PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse execute(LoginRequest request) {
        Email email = mapper.toEmail(request.getEmail());
        PlainPassword plainPassword = mapper.toPlainPassword(request.getPassword());

        // Buscar usuario
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales inválidas"));

        // Verificar password
        boolean authenticated = user.authenticate(plainPassword, passwordEncoder);
        if (!authenticated) {
            log.warn("Intento de login fallido para: {}", email.getValue());
            throw new InvalidCredentialsException("Credenciales inválidas");
        }

        // Verificar que el usuario esté activo
        if (!user.isActive()) {
            throw new UserNotActiveException("Cuenta inactiva. Contacte al administrador.");
        }

        // Generar tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        // Guardar última conexión
        userRepository.save(user);

        log.info("Login exitoso para: {} ({})", email.getValue(), user.getRoles());

        return mapper.toLoginResponse(user, accessToken, refreshToken);
    }

    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException(String message) {
            super(message);
        }
    }

    public static class UserNotActiveException extends RuntimeException {
        public UserNotActiveException(String message) {
            super(message);
        }
    }
}

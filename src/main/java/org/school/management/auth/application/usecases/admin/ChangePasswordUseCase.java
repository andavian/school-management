package org.school.management.auth.application.usecases.admin;

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
        UserId userId = mapper.toUserId(request.getUserId());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        PlainPassword currentPassword = mapper.toPlainPassword(request.getCurrentPassword());
        PlainPassword newPassword = mapper.toPlainPassword(request.getNewPassword());

        // Cambiar password (valida el actual internamente)
        user.changePassword(currentPassword, newPassword, passwordEncoder);

        userRepository.save(user);

        // Invalidar todos los tokens existentes del usuario
        // blacklistAllUserTokens(user); // Implementar si es necesario

        log.info("Password cambiado para usuario: {}", user.getEmail().getValue());

        return ChangePasswordResponse.builder()
                .success(true)
                .message("Contraseña cambiada exitosamente")
                .build();
    }
}

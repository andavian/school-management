package org.school.management.auth.application.usecases.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.dto.requests.LoginRequest;
import org.school.management.auth.application.dto.responses.LoginResponse;
import org.school.management.auth.application.mappers.AuthApplicationMapper;
import org.school.management.auth.domain.exception.InvalidPasswordException;
import org.school.management.auth.domain.exception.UserNotActiveException;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.PlainPassword;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.RoleName;
import org.school.management.auth.infra.security.JwtTokenProvider;
import org.school.management.shared.domain.valueobjects.DNI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        log.info("Intento de login con DNI: {}", request.dni());

        // Convertir DNI del record a Value Object
        DNI dni = mapper.toDni(request.dni());
        PlainPassword plainPassword = mapper.toPlainPassword(request.password());

        // Buscar usuario por DNI
        User user = userRepository.findByDni(dni)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado con DNI: {}", request.dni());
                    return new InvalidPasswordException("Credenciales inválidas");
                });

        // Verificar password y autenticar
        try {
            boolean authenticated = user.authenticate(plainPassword, passwordEncoder);
            if (!authenticated) {
                log.warn("Password incorrecto para DNI: {}", request.dni());
                throw new InvalidPasswordException("Credenciales inválidas");
            }
        } catch (UserNotActiveException e) {
            log.warn("Usuario inactivo intentó hacer login. DNI: {}", request.dni());
            throw new UserNotActiveException("Cuenta inactiva. Contacte al administrador.");
        }

        // Generar tokens JWT
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        // Guardar última conexión
        User updatedUser = userRepository.save(user);

        log.info("Login exitoso para DNI: {} con roles: {}",
                request.dni(),
                updatedUser.getRoles().stream().map(RoleName::getName).toList());

        return mapper.toLoginResponse(updatedUser, accessToken, refreshToken);
    }

    }

package org.school.management.auth.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.dto.requests.CreateUserRequest;
import org.school.management.auth.application.dto.responses.CreateUserResponse;
import org.school.management.auth.domain.exception.DniAlreadyExistsException;
import org.school.management.auth.domain.model.Role;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.RoleRepository;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.PlainPassword;
import org.school.management.auth.domain.valueobject.RoleName;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Caso de uso: crear un {@link User} con un rol dado.
 *
 * <p>Responsabilidad exclusiva: validar unicidad de DNI, construir el {@link User}
 * y persistirlo. No sabe si el usuario es teacher, student o parent — eso es
 * responsabilidad del orquestador en cada bounded context.</p>
 *
 * <p>Quién llama a este use case:</p>
 * <ul>
 *   <li>{@code teachers/CreateTeacherUseCase} — paso de creación del User con rol TEACHER</li>
 *   <li>{@code students/CreateStudentUseCase} — paso 7 del flujo de 15 pasos</li>
 *   <li>{@code parents/CreateParentUseCase} — paso de creación del User con rol PARENT</li>
 * </ul>
 *
 * <p>La password ya viene hasheada externamente — el orquestador la genera
 * porque también la necesita para enviarla por email o devolverla al admin.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreateUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HashedPassword.PasswordEncoder passwordEncoder;

    @Transactional
    public CreateUserResponse execute(CreateUserRequest request) {
        log.info("Creating user — DNI: {}, role: {}", request.dni(), request.roleName());

        Dni dni = Dni.of(request.dni());

        if (userRepository.existsByDni(dni)) {
            log.warn("User already exists with DNI: {}", request.dni());
            throw new DniAlreadyExistsException(
                    "Ya existe un usuario con el DNI: " + request.dni());
        }

        Role role = roleRepository.findByName(RoleName.of(request.roleName()))
                .orElseThrow(() -> new IllegalStateException(
                        "Rol no encontrado: " + request.roleName()));

        User user = User.create(
                dni,
                PlainPassword.of(request.plainPassword()),
                Set.of(role),
                passwordEncoder
        );

        // Los teachers inician inactivos — el orquestador lo indica explícitamente
        if (!request.startActive()) {
            user.deactivate();
        }

        User saved = userRepository.save(user);

        log.info("User created successfully — DNI: {}, userId: {}, active: {}",
                request.dni(), saved.getUserId().asString(), saved.getActive());

        return new CreateUserResponse(saved.getUserId().value());
    }
}
package org.school.management.auth.application.mappers;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.school.management.auth.application.dto.requests.CreateStudentRequest;
import org.school.management.auth.application.dto.requests.CreateTeacherRequest;
import org.school.management.auth.application.dto.responses.LoginResponse;
import org.school.management.auth.application.dto.responses.UserResponse;
import org.school.management.auth.domain.model.Role;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.RoleRepository;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.PlainPassword;
import org.school.management.auth.domain.valueobject.RoleName;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.person.domain.valueobject.DNI;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AuthApplicationMapper {

    // --- User -> UserResponse (ACTUALIZADO) ---
    @Mapping(source = "userId.value", target = "userId")
    @Mapping(source = "dni.value", target = "dni")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToStrings")
    UserResponse toUserResponse(User user);

    // --- LoginResponse (Sin cambios, pero revisa tu DTO) ---
    @Mapping(source = "user", target = "user")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "refreshToken", target = "refreshToken")
    @Mapping(constant = "3600L", target = "expiresIn")
    @Mapping(constant = "Bearer", target = "tokenType")
    LoginResponse toLoginResponse(User user, String token, String refreshToken);

    // --- Helpers para Value Objects (Sin cambios) ---
    default DNI toDni(String dni) {
        return DNI.of(dni);
    }

    default PlainPassword toPlainPassword(String password) {
        return PlainPassword.of(password);
    }

    default UserId toUserId(String userId) {
        return UserId.from(userId);
    }

    // --- Helper para Roles (ACTUALIZADO) ---
    @Named("rolesToStrings")
    default Set<String> rolesToStrings(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of();
        }
        return roles.stream()
                .map(role -> role.getName().getName()) // Extrae el String del RoleName dentro de Role
                .collect(Collectors.toSet());
    }

    // --- Factory Methods para User (¡LA MAGIA ESTÁ AQUÍ!) ---

    default User createTeacherFromRequest(CreateTeacherRequest request,
                                          PlainPassword password,
                                          HashedPassword.PasswordEncoder encoder,
                                          @Context RoleRepository roleRepository) { // <-- ¡Inyección con @Context!
        DNI dni = DNI.of(request.dni());

        // Busca el rol de dominio completo usando el repositorio
        Role teacherRole = roleRepository.findByName(RoleName.teacher())
                .orElseThrow(() -> new IllegalStateException("TEACHER role not found in the database."));

        Set<Role> roles = Set.of(teacherRole);

        User teacher = User.create(dni, password, roles, encoder);
        teacher.deactivate(); // Los profesores inician inactivos
        return teacher;
    }

    default User createStudentFromRequest(CreateStudentRequest request,
                                          PlainPassword password,
                                          HashedPassword.PasswordEncoder encoder,
                                          @Context RoleRepository roleRepository) { // <-- ¡Inyección con @Context!
        DNI dni = DNI.of(request.dni());

        // Busca el rol de dominio completo usando el repositorio
        Role studentRole = roleRepository.findByName(RoleName.student())
                .orElseThrow(() -> new IllegalStateException("STUDENT role not found in la base de datos."));

        Set<Role> roles = Set.of(studentRole);

        return User.create(dni, password, roles, encoder);
    }
}
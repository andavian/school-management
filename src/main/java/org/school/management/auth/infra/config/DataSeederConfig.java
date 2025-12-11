/*
package org.school.management.auth.infra.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.model.Role;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.RoleRepository;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.PlainPassword;
import org.school.management.auth.domain.valueobject.RoleName;
import org.school.management.shared.person.domain.valueobject.DNI;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeederConfig {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final HashedPassword.PasswordEncoder passwordEncoder;

    @Bean
    @Profile("dev") // Solo en desarrollo
    public CommandLineRunner seedData() {
        return args -> {
            log.info("🌱 Iniciando seed de datos para desarrollo...");

            // Admin principal
            if (!userRepository.existsByDni(DNI.of("00000001"))) {
                createAdmin();
            }

            // Profesores de ejemplo
            if (!userRepository.existsByDni(DNI.of("12345678"))) {
                createTeacher("12345678",  "Juan", "Pérez");
            }

            // Estudiantes de ejemplo
            if (!userRepository.existsByDni(DNI.of("87654321"))) {
                createStudent("87654321", "María", "González");
            }

            if (!userRepository.existsByDni(DNI.of("11223344"))) {
                createStudent("11223344", "Pedro", "Rodríguez");
            }

            log.info("✅ Seed de datos completado");
            log.info("");
            log.info("╔════════════════════════════════════════════════════════╗");
            log.info("║           CREDENCIALES DE PRUEBA                       ║");
            log.info("╠════════════════════════════════════════════════════════╣");
            log.info("║ ADMIN:                                                 ║");
            log.info("║   DNI: 00000001                                        ║");
            log.info("║   Password: Admin123!                                  ║");
            log.info("║                                                        ║");
            log.info("║ PROFESOR:                                              ║");
            log.info("║   DNI: 12345678                                        ║");
            log.info("║   Password: Teacher123!                                ║");
            log.info("║                                                        ║");
            log.info("║ ESTUDIANTE 1 (sin email):                              ║");
            log.info("║   DNI: 87654321                                        ║");
            log.info("║   Password: 87654321Ipet132!                           ║");
            log.info("║                                                        ║");
            log.info("║ ESTUDIANTE 2 (con email):                              ║");
            log.info("║   DNI: 11223344                                        ║");
            log.info("║   Password: 11223344Ipet132!                           ║");
            log.info("╚════════════════════════════════════════════════════════╝");
            log.info("");
        };
    }

    private void createAdmin() {

        Role adminRole = roleRepository.findByName(RoleName.admin())
                .orElseGet(() -> roleRepository.save(Role.create(RoleName.admin())));


        Set<Role> adminRoles = Set.of(adminRole);

        User adminUser = User.create(
                DNI.of("00000001"),
                PlainPassword.of("Admin123!"),
                adminRoles, // <-- Pasa el Set<Role>
                passwordEncoder
        );
        adminUser.activate();
        userRepository.save(adminUser);
        log.info("✓ Admin user created with DNI: {}", adminUser.getDni().getValue());
    }

    private void createTeacher(String dni, String firstName, String lastName) {

        Role teacherRole = roleRepository.findByName(RoleName.teacher())
                .orElseGet(() -> roleRepository.save(Role.create(RoleName.teacher())));

        Set<Role> teacherRoles = Set.of(teacherRole);

        User teacher = User.create(
                DNI.of(dni),
                PlainPassword.of("Teacher123!"),
                teacherRoles,
                passwordEncoder
        );
        teacher.activate(); // Profesores de prueba ya activos
        userRepository.save(teacher);
        log.info("✓ Profesor creado: {} {} - DNI {}", firstName, lastName, dni);
    }

    private void createStudent(String dni, String firstName, String lastName) {
        Role StudentRole = roleRepository.findByName(RoleName.student())
                .orElseGet(() -> roleRepository.save(Role.create(RoleName.student())));

        Set<Role> studentRoles = Set.of(StudentRole);
        User student = User.create(
                    DNI.of(dni),
                    PlainPassword.of(dni + "Ipet132!"),
                    studentRoles,
                    passwordEncoder
            );
        student.activate();
        userRepository.save(student);
        log.info("✓ Estudiante creado: {} {} - DNI {}", firstName, lastName, dni);
    }
}

*/

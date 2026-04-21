package org.school.management.teachers.infrastructure.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.valueobject.RoleName;
import org.school.management.auth.infra.persistence.entity.RoleEntity;
import org.school.management.auth.infra.persistence.entity.UserEntity;
import org.school.management.auth.infra.persistence.repository.RoleJpaRepository;
import org.school.management.auth.infra.persistence.repository.UserJpaRepository;
import org.school.management.geography.infra.persistence.repository.PlaceJpaRepository;
import org.school.management.teachers.domain.valueobject.EmploymentStatus;
import org.school.management.teachers.domain.valueobject.EmploymentType;
import org.school.management.teachers.infrastructure.persistence.entity.TeacherEntity;
import org.school.management.teachers.infrastructure.persistence.repository.TeacherJpaRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Seeder para el módulo de Profesores del IPET 132.
 *
 * Siembra 3 profesores con cuentas ya activadas para pruebas en perfil dev:
 *   - Juan García         — DNI 12345678 — Matemática     — Teacher123!
 *   - María López         — DNI 23456789 — Física         — Teacher123!
 *   - Carlos Fernández    — DNI 34567890 — Electrotecnia  — Teacher123!
 *
 * Los place_id se resuelven en runtime via searchByName() ya que geography
 * usa UUIDs dinámicos en las migraciones SQL.
 *
 * @Order(7) — ejecuta después de CourseDataSeeder (@Order(6))
 */
@Component
@Profile("dev")
@Order(7)
@RequiredArgsConstructor
@Slf4j
public class TeacherDataSeeder implements ApplicationRunner {

    private final TeacherJpaRepository teacherRepository;
    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;
    private final PlaceJpaRepository placeRepository;
    private final PasswordEncoder passwordEncoder;

    // UUIDs fijos — referenciables desde otros seeders
    public static final UUID TEACHER_1_ID      = UUID.fromString("0c000000-0000-0000-0000-000000000001");
    public static final UUID TEACHER_2_ID      = UUID.fromString("0c000000-0000-0000-0000-000000000002");
    public static final UUID TEACHER_3_ID      = UUID.fromString("0c000000-0000-0000-0000-000000000003");

    public static final UUID TEACHER_1_USER_ID = UUID.fromString("e0000000-0000-0000-0000-000000000001");
    public static final UUID TEACHER_2_USER_ID = UUID.fromString("e0000000-0000-0000-0000-000000000002");
    public static final UUID TEACHER_3_USER_ID = UUID.fromString("e0000000-0000-0000-0000-000000000003");

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("=".repeat(80));
        log.info("Starting Teacher Data Seeder...");
        log.info("=".repeat(80));

        if (teacherRepository.count() > 0) {
            log.info("Teacher data already exists. Skipping seeder.");
            return;
        }

        try {
            UUID cordobaPlaceId = resolveCordobaPlaceId();
            UUID adminUserId    = resolveAdminUserId();
            RoleEntity teacherRole = resolveRole(RoleName.teacher());

            seedTeachers(cordobaPlaceId, adminUserId, teacherRole);

            log.info("=".repeat(80));
            log.info("Teacher Data Seeder completed successfully!");
            log.info("Teacher Statistics:");
            log.info("  - Teachers total: {}", teacherRepository.count());
            log.info("=".repeat(80));
        } catch (Exception e) {
            log.error("Error seeding teacher data", e);
            throw e;
        }
    }

    // ── Resolución de dependencias en runtime ─────────────────────────────

    /**
     * searchByName devuelve List (LIKE %Córdoba%) — filtramos nombre exacto
     * para no matchear "Alta Córdoba", "Nueva Córdoba", etc.
     */
    private UUID resolveCordobaPlaceId() {
        return placeRepository.searchByName("Córdoba")
                .stream()
                .filter(p -> p.getName().equalsIgnoreCase("Córdoba"))
                .findFirst()
                .map(p -> p.getPlaceId())
                .orElseThrow(() -> new IllegalStateException(
                        "Place 'Córdoba' not found. Run V20 geography migration first."));
    }

    private UUID resolveAdminUserId() {
        return userRepository.findByDni("10000001")
                .map(u -> u.getUserId())
                .orElseThrow(() -> new IllegalStateException(
                        "Admin user (DNI 00000001) not found. Run V3 migration first."));
    }

    private RoleEntity resolveRole(RoleName roleName) {
        String searchName = roleName.toDbName(); // Buscará "TEACHER"
        return roleRepository.findByName(searchName)
                .orElseThrow(() -> new IllegalStateException(
                        "Role '" + searchName + "' not found. Check V1 migration."));
    }

    // ── Seed ──────────────────────────────────────────────────────────────

    private void seedTeachers(UUID placeId, UUID createdByUserId, RoleEntity teacherRole) {
        log.info("Seeding teachers...");

        List<TeacherData> teachers = List.of(
                new TeacherData(
                        TEACHER_1_ID, TEACHER_1_USER_ID,
                        "Juan", "García",
                        "12345678", "20123456782", // DV: 2
                        "juan.garcia@ipet132.edu.ar", "3514001001",
                        LocalDate.of(1980, 5, 15),
                        "Matemática", "Teacher123!"
                ),
                new TeacherData(
                        TEACHER_2_ID, TEACHER_2_USER_ID,
                        "María", "López",
                        "23456789", "27234567894", // DV: 4
                        "maria.lopez@ipet132.edu.ar", "3514002002",
                        LocalDate.of(1985, 8, 22),
                        "Física", "Teacher123!"
                ),
                new TeacherData(
                        TEACHER_3_ID, TEACHER_3_USER_ID,
                        "Carlos", "Fernández",
                        "34567890", "20345678902", // DV: 2
                        "carlos.fernandez@ipet132.edu.ar", "3514003003",
                        LocalDate.of(1978, 3, 10),
                        "Electrotecnia", "Teacher123!"
                )
        );

        for (TeacherData data : teachers) {
            UserEntity user = buildUserEntity(data, teacherRole);
            userRepository.save(user);

            TeacherEntity teacher = buildTeacherEntity(data, placeId, createdByUserId);
            teacherRepository.save(teacher);

            log.info("  ✓ Teacher: {} {} — DNI: {} — Especialización: {}",
                    data.firstName(), data.lastName(), data.dni(), data.specialization());
        }

        log.info("✓ Created {} teachers", teachers.size());
    }

    // ── Builders ──────────────────────────────────────────────────────────

    private UserEntity buildUserEntity(TeacherData data, RoleEntity teacherRole) {
        return UserEntity.builder()
                .userId(data.userId())
                .dni(data.dni())
                .password(passwordEncoder.encode(data.password()))
                .roles(Set.of(teacherRole))
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private TeacherEntity buildTeacherEntity(TeacherData data, UUID placeId,
                                             UUID createdByUserId) {
        TeacherEntity e = new TeacherEntity();
        e.setTeacherId(data.teacherId());
        e.setUserId(data.userId());
        e.setFirstName(data.firstName());
        e.setLastName(data.lastName());
        e.setDni(data.dni());
        e.setCuil(data.cuil());
        e.setEmail(data.email());
        e.setPhone(data.phone());
        e.setBirthDate(data.birthDate());
        e.setBirthPlaceId(placeId);
        e.setGender(null);
        e.setNationality("Argentina");
        e.setPlaceId(placeId);
        e.setSpecialization(data.specialization());
        e.setHireDate(LocalDate.of(2025, 3, 1));
        e.setEmploymentStatus(EmploymentStatus.ACTIVE);
        e.setEmploymentType(EmploymentType.FULL_TIME);
        e.setActive(true);
        e.setActivatedAt(LocalDateTime.now());
        e.setCreatedAt(LocalDateTime.now());
        e.setUpdatedAt(LocalDateTime.now());
        e.setCreatedBy(createdByUserId);
        return e;
    }

    private record TeacherData(
            UUID teacherId,
            UUID userId,
            String firstName,
            String lastName,
            String dni,
            String cuil,
            String email,
            String phone,
            LocalDate birthDate,
            String specialization,
            String password
    ) {}
}
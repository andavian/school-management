package org.school.management.students.personal.infrastructure.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.infra.seeder.AcademicDataSeeder;
import org.school.management.auth.domain.valueobject.RoleName;
import org.school.management.auth.infra.persistence.entity.RoleEntity;
import org.school.management.auth.infra.persistence.entity.UserEntity;
import org.school.management.auth.infra.persistence.repository.RoleJpaRepository;
import org.school.management.auth.infra.persistence.repository.UserJpaRepository;
import org.school.management.geography.infra.persistence.repository.PlaceJpaRepository;
import org.school.management.shared.person.domain.valueobject.Gender;
import org.school.management.students.enrollment.domain.valueobject.EnrollmentStatus;
import org.school.management.students.enrollment.domain.valueobject.EnrollmentType;
import org.school.management.students.enrollment.infrastructure.persistence.entity.StudentEnrollmentEntity;
import org.school.management.students.enrollment.infrastructure.persistence.repository.StudentEnrollmentJpaRepository;
import org.school.management.students.health.infrastructure.persistence.entity.StudentHealthRecordEntity;
import org.school.management.students.health.infrastructure.persistence.repository.StudentHealthRecordJpaRepository;
import org.school.management.students.parents.domain.valueobject.ParentRelationship;
import org.school.management.students.parents.infrastructure.persistence.entity.ParentEntity;
import org.school.management.students.parents.infrastructure.persistence.entity.StudentParentEntity;
import org.school.management.students.parents.infrastructure.persistence.repository.ParentJpaRepository;
import org.school.management.students.parents.infrastructure.persistence.repository.StudentParentJpaRepository;
import org.school.management.students.personal.infrastructure.persistence.entity.StudentPersonalDataEntity;
import org.school.management.students.personal.infrastructure.persistence.repository.StudentPersonalDataJpaRepository;
import org.school.management.students.records.domain.valueobject.RecordStatus;
import org.school.management.students.records.infrastructure.persistence.entity.StudentRecordEntity;
import org.school.management.students.records.infrastructure.persistence.repository.StudentRecordJpaRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Seeder para Students y Parents del IPET 132.
 *
 * Inserta directamente via JPA — NO usa CreateStudentUseCase para evitar
 * efectos secundarios (emails, FolioAssignmentService, etc.)
 *
 * Siembra 4 alumnos con sus padres:
 *   - Lucas Romero  — DNI 11223344 — 1°A — con email    (credencial en CLAUDE.md)
 *   - Sofía Torres  — DNI 87654321 — 1°A — sin email    (credencial en CLAUDE.md)
 *   - Martín Díaz   — DNI 44556677 — 4°A — Electricista
 *   - Ana Gómez     — DNI 55667788 — 4°C — Electromecánico
 *
 * Los place_id se resuelven en runtime via searchByName() ya que geography
 * usa UUIDs dinámicos en las migraciones SQL.
 *
 * @Order(8) — ejecuta después de TeacherDataSeeder (@Order(7))
 */
@Component
@Profile("dev")
@Order(8)
@RequiredArgsConstructor
@Slf4j
public class StudentAndParentDataSeeder implements ApplicationRunner {

    private final StudentPersonalDataJpaRepository studentRepository;
    private final StudentHealthRecordJpaRepository healthRepository;
    private final StudentRecordJpaRepository recordRepository;
    private final StudentEnrollmentJpaRepository enrollmentRepository;
    private final ParentJpaRepository parentRepository;
    private final StudentParentJpaRepository studentParentRepository;
    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;
    private final PlaceJpaRepository placeRepository;
    private final PasswordEncoder passwordEncoder;

    // ── UUIDs fijos — Students ────────────────────────────────────────────
    public static final UUID STUDENT_1_ID      = UUID.fromString("ed000000-0000-0000-0000-000000000001");
    public static final UUID STUDENT_2_ID      = UUID.fromString("ed000000-0000-0000-0000-000000000002");
    public static final UUID STUDENT_3_ID      = UUID.fromString("ed000000-0000-0000-0000-000000000003");
    public static final UUID STUDENT_4_ID      = UUID.fromString("ed000000-0000-0000-0000-000000000004");

    public static final UUID STUDENT_1_USER_ID = UUID.fromString("ea000000-0000-0000-0000-000000000001");
    public static final UUID STUDENT_2_USER_ID = UUID.fromString("ea000000-0000-0000-0000-000000000002");
    public static final UUID STUDENT_3_USER_ID = UUID.fromString("ea000000-0000-0000-0000-000000000003");
    public static final UUID STUDENT_4_USER_ID = UUID.fromString("ea000000-0000-0000-0000-000000000004");

    // ── UUIDs fijos — Parents ─────────────────────────────────────────────
    public static final UUID PARENT_1_ID      = UUID.fromString("fa000000-0000-0000-0000-000000000001");
    public static final UUID PARENT_2_ID      = UUID.fromString("fa000000-0000-0000-0000-000000000002");
    public static final UUID PARENT_3_ID      = UUID.fromString("fa000000-0000-0000-0000-000000000003");
    public static final UUID PARENT_4_ID      = UUID.fromString("fa000000-0000-0000-0000-000000000004");

    public static final UUID PARENT_1_USER_ID = UUID.fromString("fe000000-0000-0000-0000-000000000001");
    public static final UUID PARENT_2_USER_ID = UUID.fromString("fe000000-0000-0000-0000-000000000002");
    public static final UUID PARENT_3_USER_ID = UUID.fromString("fe000000-0000-0000-0000-000000000003");
    public static final UUID PARENT_4_USER_ID = UUID.fromString("fe000000-0000-0000-0000-000000000004");

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("=".repeat(80));
        log.info("Starting Student & Parent Data Seeder...");
        log.info("=".repeat(80));

        if (studentRepository.count() > 0) {
            log.info("Student data already exists. Skipping seeder.");
            return;
        }

        try {
            UUID cordobaPlaceId  = resolveCordobaPlaceId();
            UUID adminUserId     = resolveAdminUserId();
            RoleEntity studentRole = resolveRole(RoleName.student());
            RoleEntity parentRole  = resolveRole(RoleName.parent());

            seedStudentsAndParents(cordobaPlaceId, adminUserId, studentRole, parentRole);

            log.info("=".repeat(80));
            log.info("Student & Parent Data Seeder completed successfully!");
            logStatistics();
            log.info("=".repeat(80));
        } catch (Exception e) {
            log.error("Error seeding student/parent data", e);
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
        return userRepository.findByDni("00000001")
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

    // ── Seed principal ────────────────────────────────────────────────────

    private void seedStudentsAndParents(UUID placeId, UUID adminUserId,
                                        RoleEntity studentRole, RoleEntity parentRole) {
        log.info("Seeding students and parents...");

        // Alumno 1: Lucas Romero — 1°A — CON email (credencial en CLAUDE.md)
        seedStudent(
                STUDENT_1_ID, STUDENT_1_USER_ID,
                "Lucas", "Romero",
                "11223344", "20112233444",
                "lucas.romero@gmail.com",
                LocalDate.of(2010, 3, 20),
                AcademicDataSeeder.GL_2025_1A,
                "11223344Ipet132!",
                placeId, adminUserId, studentRole,
                PARENT_1_ID, PARENT_1_USER_ID,
                "Roberto", "Romero",
                "98765432", "20987654325",
                "roberto.romero@gmail.com", "3514101010",
                ParentRelationship.FATHER, parentRole
        );

        // Alumno 2: Sofía Torres — 1°A — SIN email (credencial en CLAUDE.md)
        seedStudent(
                STUDENT_2_ID, STUDENT_2_USER_ID,
                "Sofía", "Torres",
                "87654321", "27876543211",
                null,
                LocalDate.of(2010, 7, 5),
                AcademicDataSeeder.GL_2025_1A,
                "87654321Ipet132!",
                placeId, adminUserId, studentRole,
                PARENT_2_ID, PARENT_2_USER_ID,
                "Carmen", "Torres",
                "76543210", "27765432103",
                "carmen.torres@gmail.com", "3514202020",
                ParentRelationship.MOTHER, parentRole
        );

        // Alumno 3: Martín Díaz — 4°A — Técnico Electricista
        seedStudent(
                STUDENT_3_ID, STUDENT_3_USER_ID,
                "Martín", "Díaz",
                "44556677", "20445566774",
                "martin.diaz@gmail.com",
                LocalDate.of(2007, 11, 12),
                AcademicDataSeeder.GL_2025_4A,
                "44556677Ipet132!",
                placeId, adminUserId, studentRole,
                PARENT_3_ID, PARENT_3_USER_ID,
                "Hugo", "Díaz",
                "33445566", "20334455669",
                "hugo.diaz@gmail.com", "3514303030",
                ParentRelationship.FATHER, parentRole
        );

        // Alumno 4: Ana Gómez — 4°C — Técnico Electromecánico
        seedStudent(
                STUDENT_4_ID, STUDENT_4_USER_ID,
                "Ana", "Gómez",
                "55667788", "27556677888",
                "ana.gomez@gmail.com",
                LocalDate.of(2007, 4, 28),
                AcademicDataSeeder.GL_2025_4C,
                "55667788Ipet132!",
                placeId, adminUserId, studentRole,
                PARENT_4_ID, PARENT_4_USER_ID,
                "Patricia", "Gómez",
                "66778899", "27667788999",
                "patricia.gomez@gmail.com", "3514404040",
                ParentRelationship.MOTHER, parentRole
        );

        log.info("✓ Seeded 4 students with their parents");
    }

    private void seedStudent(
            UUID studentId, UUID studentUserId,
            String firstName, String lastName,
            String dni, String cuil, String email,
            LocalDate birthDate, UUID gradeLevelId,
            String rawPassword, UUID placeId, UUID adminUserId,
            RoleEntity studentRole,
            UUID parentId, UUID parentUserId,
            String parentFirstName, String parentLastName,
            String parentDni, String parentCuil,
            String parentEmail, String parentPhone,
            ParentRelationship relationship, RoleEntity parentRole) {

        // 1. User del alumno con rol STUDENT
        userRepository.save(buildUserEntity(
                studentUserId, dni, rawPassword, studentRole));

        // 2. StudentPersonalData
        studentRepository.save(buildStudent(
                studentId, studentUserId, firstName, lastName,
                dni, cuil, email, birthDate, placeId, adminUserId));

        // 3. StudentHealthRecord — contacto de emergencia = el padre
        healthRepository.save(buildHealthRecord(
                studentId, parentFirstName, parentLastName, parentPhone));

        // 4. StudentRecord — folio 0 en dev (no usa FolioAssignmentService)
        recordRepository.save(buildStudentRecord(studentId, dni));

        // 5. StudentEnrollment
        enrollmentRepository.save(buildEnrollment(studentId, gradeLevelId));

        // 6. Parent — reusar si ya existe por DNI
        ParentEntity parent = parentRepository.findByDni(parentDni)
                .orElseGet(() -> {
                    userRepository.save(buildUserEntity(
                            parentUserId, parentDni, "Parent123!", parentRole));
                    return parentRepository.save(buildParent(
                            parentId, parentUserId,
                            parentFirstName, parentLastName,
                            parentDni, parentCuil,
                            parentEmail, parentPhone,
                            placeId, adminUserId));
                });

        // 7. StudentParent
        studentParentRepository.save(
                buildStudentParent(studentId, parent.getParentId(), relationship));

        log.info("  ✓ Student: {} {} — DNI: {} — Curso: {}",
                firstName, lastName, dni, gradeLevelId);
    }

    // ── Builders ──────────────────────────────────────────────────────────

    private UserEntity buildUserEntity(UUID userId, String dni,
                                       String rawPassword, RoleEntity role) {
        return UserEntity.builder()
                .userId(userId)
                .dni(dni)
                .password(passwordEncoder.encode(rawPassword))
                .roles(Set.of(role))
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private StudentPersonalDataEntity buildStudent(
            UUID studentId, UUID userId,
            String firstName, String lastName,
            String dni, String cuil, String email,
            LocalDate birthDate, UUID placeId, UUID createdBy) {

        StudentPersonalDataEntity e = new StudentPersonalDataEntity();
        e.setStudentId(studentId);
        e.setUserId(userId);
        e.setDni(dni);
        e.setCuil(cuil);
        e.setFirstName(firstName);
        e.setLastName(lastName);
        e.setBirthDate(birthDate);
        e.setBirthPlaceId(placeId);
        e.setGender(Gender.OTHER);
        e.setNationality("Argentina");
        e.setEmail(email);
        e.setPhone("3510000000");
        e.setAddressStreet("Av. Colón");
        e.setAddressNumber("100");
        e.setResidencePlaceId(placeId);
        e.setCreatedAt(LocalDateTime.now());
        e.setUpdatedAt(LocalDateTime.now());
        e.setCreatedBy(createdBy);
        return e;
    }

    private StudentHealthRecordEntity buildHealthRecord(UUID studentId,
                                                        String contactFirstName,
                                                        String contactLastName,
                                                        String contactPhone) {
        StudentHealthRecordEntity e = new StudentHealthRecordEntity();
        e.setHealthRecordId(UUID.randomUUID());
        e.setStudentId(studentId);
        // emergency_contact_name concatenado — mismo criterio que el mapper de health
        e.setEmergencyContactName(contactFirstName + " " + contactLastName);
        e.setEmergencyContactPhone(contactPhone);
        e.setCreatedAt(LocalDateTime.now());
        e.setUpdatedAt(LocalDateTime.now());
        return e;
    }

    private StudentRecordEntity buildStudentRecord(UUID studentId, String dni) {
        StudentRecordEntity e = new StudentRecordEntity();
        e.setRecordId(UUID.randomUUID());
        e.setStudentId(studentId);
        e.setAcademicYearId(AcademicDataSeeder.ACADEMIC_YEAR_2025_ID);
        e.setRecordNumber(dni);                        // RecordNumber = DNI
        e.setRegistryId(AcademicDataSeeder.REGISTRY_2025_ID);
        e.setFolioNumber(0);                           // folio 0 en dev
        e.setStatus(RecordStatus.APPROVED);
        e.setCreatedAt(LocalDateTime.now());
        e.setUpdatedAt(LocalDateTime.now());
        return e;
    }

    private StudentEnrollmentEntity buildEnrollment(UUID studentId, UUID gradeLevelId) {
        StudentEnrollmentEntity e = new StudentEnrollmentEntity();
        e.setEnrollmentId(UUID.randomUUID());
        e.setStudentId(studentId);
        e.setAcademicYearId(AcademicDataSeeder.ACADEMIC_YEAR_2025_ID);
        e.setGradeLevelId(gradeLevelId);
        e.setEnrollmentDate(LocalDate.of(2025, 3, 3));
        e.setEnrollmentType(EnrollmentType.NEW);
        e.setStatus(EnrollmentStatus.ACTIVE);
        e.setRepeating(false);
        e.setCreatedAt(LocalDateTime.now());
        e.setUpdatedAt(LocalDateTime.now());
        return e;
    }

    private ParentEntity buildParent(
            UUID parentId, UUID userId,
            String firstName, String lastName,
            String dni, String cuil,
            String email, String phone,
            UUID placeId, UUID createdBy) {

        ParentEntity e = new ParentEntity();
        e.setParentId(parentId);
        e.setUserId(userId);
        e.setFirstName(firstName);
        e.setLastName(lastName);
        e.setDni(dni);
        e.setCuil(cuil);
        e.setEmail(email);
        e.setPhone(phone);
        e.setPlaceId(placeId);
        e.setNationality("Argentina");
        e.setActive(true);
        e.setCreatedAt(LocalDateTime.now());
        e.setUpdatedAt(LocalDateTime.now());
        e.setCreatedBy(createdBy);
        return e;
    }

    private StudentParentEntity buildStudentParent(UUID studentId,
                                                   UUID parentId,
                                                   ParentRelationship relationship) {
        StudentParentEntity e = new StudentParentEntity();
        e.setStudentParentId(UUID.randomUUID());
        e.setStudentId(studentId);
        e.setParentId(parentId);
        e.setRelationship(relationship);
        e.setPrimaryContact(true);
        e.setAuthorizedPickup(true);
        e.setEmergencyContact(true);
        e.setCreatedAt(LocalDateTime.now());
        return e;
    }

    private void logStatistics() {
        log.info("Student & Parent Statistics:");
        log.info("  - Students:             {}", studentRepository.count());
        log.info("  - Health records:       {}", healthRepository.count());
        log.info("  - Student records:      {}", recordRepository.count());
        log.info("  - Enrollments:          {}", enrollmentRepository.count());
        log.info("  - Parents:              {}", parentRepository.count());
        log.info("  - Student-Parent links: {}", studentParentRepository.count());
    }
}
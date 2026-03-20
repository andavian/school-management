package org.school.management.academic.infra.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.domain.valueobject.enums.AcademicYearStatus;
import org.school.management.academic.infra.persistence.entity.*;
import org.school.management.academic.infra.persistence.repository.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Seeder para el módulo Académico del IPET 132.
 * Siembra:
 * - 2 años lectivos: 2024 (CLOSED) y 2025 (ACTIVE)
 * - 2 orientaciones: Técnico Electricista y Técnico Electromecánico (desde 4°)
 * - 22 cursos: 1°-2° con divisiones A/B/C/D, 3°-6° con A/B/C, 7° con A/B — por cada año lectivo
 * - ~60 materias: ciclo básico (1°-3°) + ciclo orientado por orientación (4°-7°)
 * - 1 registro de calificaciones activo por año lectivo
 *
 * UUIDs fijos para poder ser referenciados desde CourseDataSeeder (@Order(6)).
 *
 * @Order(5) — ejecuta después de geography seeder (@Order(4)) y antes de course seeder (@Order(6))
 */
@Component
@Profile("dev")
@Order(5)
@RequiredArgsConstructor
@Slf4j
public class AcademicDataSeeder implements ApplicationRunner {

    private final AcademicYearJpaRepository academicYearRepository;
    private final OrientationJpaRepository orientationRepository;
    private final GradeLevelJpaRepository gradeLevelRepository;
    private final SubjectJpaRepository subjectRepository;
    private final QualificationRegistryJpaRepository registryRepository;

    // =========================================================================
    // UUIDs FIJOS — Academic Years (CORREGIDOS - hex válido)
    // =========================================================================
    public static final UUID ACADEMIC_YEAR_2024_ID = UUID.fromString("a0000000-0000-0000-0000-000000002024");
    public static final UUID ACADEMIC_YEAR_2025_ID = UUID.fromString("a0000000-0000-0000-0000-000000002025");

    // =========================================================================
    // UUIDs FIJOS — Orientations (CORREGIDOS - hex válido)
    // =========================================================================
    public static final UUID ORIENTATION_ELECTRICISTA_ID = UUID.fromString("00000001-0000-0000-0000-000000000001");
    public static final UUID ORIENTATION_ELECTROMECANICO_ID = UUID.fromString("00000001-0000-0000-0000-000000000002");

    // =========================================================================
    // UUIDs FIJOS — Grade Levels 2025 (CORREGIDOS - hex válido)
    // Patrón: 0125-AADD (AA = año, DD = división)
    // 1°A-D, 2°A-D, 3°A-C, 4°A-C, 5°A-C, 6°A-C, 7°A-B
    // =========================================================================
    public static final UUID GL_2025_1A = UUID.fromString("01250000-0000-0000-0000-000000000101");
    public static final UUID GL_2025_1B = UUID.fromString("01250000-0000-0000-0000-000000000102");
    public static final UUID GL_2025_1C = UUID.fromString("01250000-0000-0000-0000-000000000103");
    public static final UUID GL_2025_1D = UUID.fromString("01250000-0000-0000-0000-000000000104");
    public static final UUID GL_2025_2A = UUID.fromString("01250000-0000-0000-0000-000000000201");
    public static final UUID GL_2025_2B = UUID.fromString("01250000-0000-0000-0000-000000000202");
    public static final UUID GL_2025_2C = UUID.fromString("01250000-0000-0000-0000-000000000203");
    public static final UUID GL_2025_2D = UUID.fromString("01250000-0000-0000-0000-000000000204");
    public static final UUID GL_2025_3A = UUID.fromString("01250000-0000-0000-0000-000000000301");
    public static final UUID GL_2025_3B = UUID.fromString("01250000-0000-0000-0000-000000000302");
    public static final UUID GL_2025_3C = UUID.fromString("01250000-0000-0000-0000-000000000303");
    public static final UUID GL_2025_4A = UUID.fromString("01250000-0000-0000-0000-000000000401");
    public static final UUID GL_2025_4B = UUID.fromString("01250000-0000-0000-0000-000000000402");
    public static final UUID GL_2025_4C = UUID.fromString("01250000-0000-0000-0000-000000000403");
    public static final UUID GL_2025_5A = UUID.fromString("01250000-0000-0000-0000-000000000501");
    public static final UUID GL_2025_5B = UUID.fromString("01250000-0000-0000-0000-000000000502");
    public static final UUID GL_2025_5C = UUID.fromString("01250000-0000-0000-0000-000000000503");
    public static final UUID GL_2025_6A = UUID.fromString("01250000-0000-0000-0000-000000000601");
    public static final UUID GL_2025_6B = UUID.fromString("01250000-0000-0000-0000-000000000602");
    public static final UUID GL_2025_6C = UUID.fromString("01250000-0000-0000-0000-000000000603");
    public static final UUID GL_2025_7A = UUID.fromString("01250000-0000-0000-0000-000000000701");
    public static final UUID GL_2025_7B = UUID.fromString("01250000-0000-0000-0000-000000000702");

    // =========================================================================
    // UUIDs FIJOS — Subjects — Ciclo Básico (1°-3°, sin orientación) (CORREGIDOS)
    // =========================================================================
    public static final UUID SUBJ_MATEMATICA_1 = UUID.fromString("00010000-0000-0000-0000-000000000101");
    public static final UUID SUBJ_LENGUA_1 = UUID.fromString("00010000-0000-0000-0000-000000000102");
    public static final UUID SUBJ_HISTORIA_1 = UUID.fromString("00010000-0000-0000-0000-000000000103");
    public static final UUID SUBJ_GEOGRAFIA_1 = UUID.fromString("00010000-0000-0000-0000-000000000104");
    public static final UUID SUBJ_FISICA_1 = UUID.fromString("00010000-0000-0000-0000-000000000105");
    public static final UUID SUBJ_QUIMICA_1 = UUID.fromString("00010000-0000-0000-0000-000000000106");
    public static final UUID SUBJ_ED_FISICA_1 = UUID.fromString("00010000-0000-0000-0000-000000000107");
    public static final UUID SUBJ_FORMACION_C_1 = UUID.fromString("00010000-0000-0000-0000-000000000108");
    public static final UUID SUBJ_TECNOLOGIA_1 = UUID.fromString("00010000-0000-0000-0000-000000000109");
    public static final UUID SUBJ_INGLES_1 = UUID.fromString("00010000-0000-0000-0000-000000000110");
    public static final UUID SUBJ_MATEMATICA_2 = UUID.fromString("00010000-0000-0000-0000-000000000201");
    public static final UUID SUBJ_LENGUA_2 = UUID.fromString("00010000-0000-0000-0000-000000000202");
    public static final UUID SUBJ_HISTORIA_2 = UUID.fromString("00010000-0000-0000-0000-000000000203");
    public static final UUID SUBJ_GEOGRAFIA_2 = UUID.fromString("00010000-0000-0000-0000-000000000204");
    public static final UUID SUBJ_FISICA_2 = UUID.fromString("00010000-0000-0000-0000-000000000205");
    public static final UUID SUBJ_QUIMICA_2 = UUID.fromString("00010000-0000-0000-0000-000000000206");
    public static final UUID SUBJ_ED_FISICA_2 = UUID.fromString("00010000-0000-0000-0000-000000000207");
    public static final UUID SUBJ_FORMACION_C_2 = UUID.fromString("00010000-0000-0000-0000-000000000208");
    public static final UUID SUBJ_TECNOLOGIA_2 = UUID.fromString("00010000-0000-0000-0000-000000000209");
    public static final UUID SUBJ_INGLES_2 = UUID.fromString("00010000-0000-0000-0000-000000000210");
    public static final UUID SUBJ_MATEMATICA_3 = UUID.fromString("00010000-0000-0000-0000-000000000301");
    public static final UUID SUBJ_LENGUA_3 = UUID.fromString("00010000-0000-0000-0000-000000000302");
    public static final UUID SUBJ_HISTORIA_3 = UUID.fromString("00010000-0000-0000-0000-000000000303");
    public static final UUID SUBJ_FISICA_3 = UUID.fromString("00010000-0000-0000-0000-000000000304");
    public static final UUID SUBJ_QUIMICA_3 = UUID.fromString("00010000-0000-0000-0000-000000000305");
    public static final UUID SUBJ_ED_FISICA_3 = UUID.fromString("00010000-0000-0000-0000-000000000306");
    public static final UUID SUBJ_FORMACION_C_3 = UUID.fromString("00010000-0000-0000-0000-000000000307");
    public static final UUID SUBJ_TECNOLOGIA_3 = UUID.fromString("00010000-0000-0000-0000-000000000308");
    public static final UUID SUBJ_INGLES_3 = UUID.fromString("00010000-0000-0000-0000-000000000309");
    public static final UUID SUBJ_DIBUJO_TEC_3 = UUID.fromString("00010000-0000-0000-0000-000000000310");

    // =========================================================================
    // UUIDs FIJOS — Subjects — Ciclo Orientado ELECTRICISTA (4°-7°) (CORREGIDOS)
    // =========================================================================
    public static final UUID SUBJ_EELEC_MAT_4 = UUID.fromString("00010000-0000-0000-0000-000000000401");
    public static final UUID SUBJ_EELEC_FISICA_4 = UUID.fromString("00010000-0000-0000-0000-000000000402");
    public static final UUID SUBJ_EELEC_LENGUA_4 = UUID.fromString("00010000-0000-0000-0000-000000000403");
    public static final UUID SUBJ_EELEC_ELEC_4 = UUID.fromString("00010000-0000-0000-0000-000000000404");
    public static final UUID SUBJ_EELEC_INST_4 = UUID.fromString("00010000-0000-0000-0000-000000000405");
    public static final UUID SUBJ_EELEC_DIBUJO_4 = UUID.fromString("00010000-0000-0000-0000-000000000406");
    public static final UUID SUBJ_EELEC_ED_FIS_4 = UUID.fromString("00010000-0000-0000-0000-000000000407");
    public static final UUID SUBJ_EELEC_MAT_5 = UUID.fromString("00010000-0000-0000-0000-000000000501");
    public static final UUID SUBJ_EELEC_FISICA_5 = UUID.fromString("00010000-0000-0000-0000-000000000502");
    public static final UUID SUBJ_EELEC_ELEC_5 = UUID.fromString("00010000-0000-0000-0000-000000000503");
    public static final UUID SUBJ_EELEC_MAQUINAS_5 = UUID.fromString("00010000-0000-0000-0000-000000000504");
    public static final UUID SUBJ_EELEC_INST_5 = UUID.fromString("00010000-0000-0000-0000-000000000505");
    public static final UUID SUBJ_EELEC_ED_FIS_5 = UUID.fromString("00010000-0000-0000-0000-000000000506");
    public static final UUID SUBJ_EELEC_MAT_6 = UUID.fromString("00010000-0000-0000-0000-000000000601");
    public static final UUID SUBJ_EELEC_ELEC_6 = UUID.fromString("00010000-0000-0000-0000-000000000602");
    public static final UUID SUBJ_EELEC_MAQUINAS_6 = UUID.fromString("00010000-0000-0000-0000-000000000603");
    public static final UUID SUBJ_EELEC_PROYECTO_6 = UUID.fromString("00010000-0000-0000-0000-000000000604");
    public static final UUID SUBJ_EELEC_LEGISL_6 = UUID.fromString("00010000-0000-0000-0000-000000000605");
    public static final UUID SUBJ_EELEC_ED_FIS_6 = UUID.fromString("00010000-0000-0000-0000-000000000606");
    public static final UUID SUBJ_EELEC_PROYECTO_7 = UUID.fromString("00010000-0000-0000-0000-000000000701");
    public static final UUID SUBJ_EELEC_ELEC_7 = UUID.fromString("00010000-0000-0000-0000-000000000702");
    public static final UUID SUBJ_EELEC_GESTION_7 = UUID.fromString("00010000-0000-0000-0000-000000000703");
    public static final UUID SUBJ_EELEC_SEGURIDAD_7 = UUID.fromString("00010000-0000-0000-0000-000000000704");

    // =========================================================================
    // UUIDs FIJOS — Subjects — Ciclo Orientado ELECTROMECANICO (4°-7°) (CORREGIDOS)
    // =========================================================================
    public static final UUID SUBJ_EMEC_MAT_4 = UUID.fromString("00010000-0000-0000-0000-000000000411");
    public static final UUID SUBJ_EMEC_FISICA_4 = UUID.fromString("00010000-0000-0000-0000-000000000412");
    public static final UUID SUBJ_EMEC_LENGUA_4 = UUID.fromString("00010000-0000-0000-0000-000000000413");
    public static final UUID SUBJ_EMEC_MECANICA_4 = UUID.fromString("00010000-0000-0000-0000-000000000414");
    public static final UUID SUBJ_EMEC_TERMOT_4 = UUID.fromString("00010000-0000-0000-0000-000000000415");
    public static final UUID SUBJ_EMEC_DIBUJO_4 = UUID.fromString("00010000-0000-0000-0000-000000000416");
    public static final UUID SUBJ_EMEC_ED_FIS_4 = UUID.fromString("00010000-0000-0000-0000-000000000417");
    public static final UUID SUBJ_EMEC_MAT_5 = UUID.fromString("00010000-0000-0000-0000-000000000511");
    public static final UUID SUBJ_EMEC_FISICA_5 = UUID.fromString("00010000-0000-0000-0000-000000000512");
    public static final UUID SUBJ_EMEC_MECANICA_5 = UUID.fromString("00010000-0000-0000-0000-000000000513");
    public static final UUID SUBJ_EMEC_MAQUINAS_5 = UUID.fromString("00010000-0000-0000-0000-000000000514");
    public static final UUID SUBJ_EMEC_HIDRO_5 = UUID.fromString("00010000-0000-0000-0000-000000000515");
    public static final UUID SUBJ_EMEC_ED_FIS_5 = UUID.fromString("00010000-0000-0000-0000-000000000516");
    public static final UUID SUBJ_EMEC_MAT_6 = UUID.fromString("00010000-0000-0000-0000-000000000611");
    public static final UUID SUBJ_EMEC_MAQUINAS_6 = UUID.fromString("00010000-0000-0000-0000-000000000612");
    public static final UUID SUBJ_EMEC_MECANICA_6 = UUID.fromString("00010000-0000-0000-0000-000000000613");
    public static final UUID SUBJ_EMEC_PROYECTO_6 = UUID.fromString("00010000-0000-0000-0000-000000000614");
    public static final UUID SUBJ_EMEC_LEGISL_6 = UUID.fromString("00010000-0000-0000-0000-000000000615");
    public static final UUID SUBJ_EMEC_ED_FIS_6 = UUID.fromString("00010000-0000-0000-0000-000000000616");
    public static final UUID SUBJ_EMEC_PROYECTO_7 = UUID.fromString("00010000-0000-0000-0000-000000000711");
    public static final UUID SUBJ_EMEC_MAQUINAS_7 = UUID.fromString("00010000-0000-0000-0000-000000000712");
    public static final UUID SUBJ_EMEC_GESTION_7 = UUID.fromString("00010000-0000-0000-0000-000000000713");
    public static final UUID SUBJ_EMEC_SEGURIDAD_7 = UUID.fromString("00010000-0000-0000-0000-000000000714");

    // =========================================================================
    // UUIDs FIJOS — Qualification Registries (CORREGIDOS)
    // =========================================================================
    public static final UUID REGISTRY_2024_ID = UUID.fromString("00000000-0000-0000-0000-000000002024");
    public static final UUID REGISTRY_2025_ID = UUID.fromString("00000000-0000-0000-0000-000000002025");

    // =========================================================================
    // RUNNER
    // =========================================================================
    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("=".repeat(80));
        log.info("Starting Academic Data Seeder...");
        log.info("=".repeat(80));

        if (academicYearRepository.count() > 0) {
            log.info("Academic data already exists. Skipping seeder.");
            return;
        }

        try {
            seedAcademicYears();
            seedOrientations();
            seedSubjects();
            seedGradeLevels();
            seedQualificationRegistries();

            log.info("=".repeat(80));
            log.info("Academic Data Seeder completed successfully!");
            logStatistics();
            log.info("=".repeat(80));
        } catch (Exception e) {
            log.error("Error seeding academic data", e);
            throw e;
        }
    }

    // =========================================================================
    // ACADEMIC YEARS
    // =========================================================================
    private void seedAcademicYears() {
        log.info("Seeding academic years...");

        academicYearRepository.save(buildAcademicYear(
                ACADEMIC_YEAR_2024_ID,
                2024,
                LocalDate.of(2024, 3, 4),
                LocalDate.of(2024, 12, 13),
                AcademicYearStatus.CLOSED
        ));
        log.info("  ✓ Academic year: 2024 (CLOSED)");

        academicYearRepository.save(buildAcademicYear(
                ACADEMIC_YEAR_2025_ID,
                2025,
                LocalDate.of(2025, 3, 3),
                LocalDate.of(2025, 12, 12),
                AcademicYearStatus.ACTIVE
        ));
        log.info("  ✓ Academic year: 2025 (ACTIVE)");

        log.info("✓ Created 2 academic years");
    }

    private AcademicYearEntity buildAcademicYear(UUID id, int year,
                                                 LocalDate start, LocalDate end,
                                                 AcademicYearStatus status) {
        AcademicYearEntity e = new AcademicYearEntity();
        e.setAcademicYearId(id);
        e.setYear(year);
        e.setStartDate(start);
        e.setEndDate(end);
        e.setStatus(status);
        e.setCreatedAt(LocalDateTime.now());
        e.setUpdatedAt(LocalDateTime.now());
        return e;
    }

    // =========================================================================
    // ORIENTATIONS
    // =========================================================================
    private void seedOrientations() {
        log.info("Seeding orientations...");

        orientationRepository.save(buildOrientation(
                ORIENTATION_ELECTRICISTA_ID,
                "Técnico Electricista",
                "TEC_ELECTRICISTA",
                "Orientación en instalaciones y sistemas eléctricos",
                4
        ));
        log.info("  ✓ Orientation: Técnico Electricista (TEC_ELECTRICISTA)");

        orientationRepository.save(buildOrientation(
                ORIENTATION_ELECTROMECANICO_ID,
                "Técnico Electromecánico",
                "TEC_ELECTROMECANICO",
                "Orientación en sistemas electromecánicos e industriales",
                4
        ));
        log.info("  ✓ Orientation: Técnico Electromecánico (TEC_ELECTROMECANICO)");

        log.info("✓ Created 2 orientations");
    }

    private OrientationEntity buildOrientation(UUID id, String name, String code,
                                               String description, int availableFromYear) {
        OrientationEntity e = new OrientationEntity();
        e.setOrientationId(id);
        e.setName(name);
        e.setCode(code);
        e.setDescription(description);
        e.setAvailableFromYear(availableFromYear);
        e.setActive(true);
        e.setCreatedAt(LocalDateTime.now());
        e.setUpdatedAt(LocalDateTime.now());
        return e;
    }

    // =========================================================================
    // SUBJECTS
    // =========================================================================
    private void seedSubjects() {
        log.info("Seeding subjects...");
        int count = 0;

        // --- Ciclo Básico 1° ---
        count += saveSubjects(List.of(
                buildSubject(SUBJ_MATEMATICA_1, "Matemática", "MAT-1", 1, null, 5),
                buildSubject(SUBJ_LENGUA_1, "Lengua y Literatura", "LEN-1", 1, null, 4),
                buildSubject(SUBJ_HISTORIA_1, "Historia", "HIS-1", 1, null, 3),
                buildSubject(SUBJ_GEOGRAFIA_1, "Geografía", "GEO-1", 1, null, 3),
                buildSubject(SUBJ_FISICA_1, "Física", "FIS-1", 1, null, 3),
                buildSubject(SUBJ_QUIMICA_1, "Química", "QUI-1", 1, null, 3),
                buildSubject(SUBJ_ED_FISICA_1, "Educación Física", "EDF-1", 1, null, 2),
                buildSubject(SUBJ_FORMACION_C_1, "Formación para la Ciudadanía", "FC-1", 1, null, 2),
                buildSubject(SUBJ_TECNOLOGIA_1, "Tecnología", "TEC-1", 1, null, 3),
                buildSubject(SUBJ_INGLES_1, "Inglés", "ING-1", 1, null, 3)
        ));

        // --- Ciclo Básico 2° ---
        count += saveSubjects(List.of(
                buildSubject(SUBJ_MATEMATICA_2, "Matemática", "MAT-2", 2, null, 5),
                buildSubject(SUBJ_LENGUA_2, "Lengua y Literatura", "LEN-2", 2, null, 4),
                buildSubject(SUBJ_HISTORIA_2, "Historia", "HIS-2", 2, null, 3),
                buildSubject(SUBJ_GEOGRAFIA_2, "Geografía", "GEO-2", 2, null, 3),
                buildSubject(SUBJ_FISICA_2, "Física", "FIS-2", 2, null, 3),
                buildSubject(SUBJ_QUIMICA_2, "Química", "QUI-2", 2, null, 3),
                buildSubject(SUBJ_ED_FISICA_2, "Educación Física", "EDF-2", 2, null, 2),
                buildSubject(SUBJ_FORMACION_C_2, "Formación para la Ciudadanía", "FC-2", 2, null, 2),
                buildSubject(SUBJ_TECNOLOGIA_2, "Tecnología", "TEC-2", 2, null, 3),
                buildSubject(SUBJ_INGLES_2, "Inglés", "ING-2", 2, null, 3)
        ));

        // --- Ciclo Básico 3° ---
        count += saveSubjects(List.of(
                buildSubject(SUBJ_MATEMATICA_3, "Matemática", "MAT-3", 3, null, 5),
                buildSubject(SUBJ_LENGUA_3, "Lengua y Literatura", "LEN-3", 3, null, 4),
                buildSubject(SUBJ_HISTORIA_3, "Historia", "HIS-3", 3, null, 3),
                buildSubject(SUBJ_FISICA_3, "Física", "FIS-3", 3, null, 4),
                buildSubject(SUBJ_QUIMICA_3, "Química", "QUI-3", 3, null, 4),
                buildSubject(SUBJ_ED_FISICA_3, "Educación Física", "EDF-3", 3, null, 2),
                buildSubject(SUBJ_FORMACION_C_3, "Formación para la Ciudadanía", "FC-3", 3, null, 2),
                buildSubject(SUBJ_TECNOLOGIA_3, "Tecnología", "TEC-3", 3, null, 3),
                buildSubject(SUBJ_INGLES_3, "Inglés", "ING-3", 3, null, 3),
                buildSubject(SUBJ_DIBUJO_TEC_3, "Dibujo Técnico", "DT-3", 3, null, 3)
        ));

        // --- Ciclo Orientado 4°-7° — Técnico Electricista ---
        count += saveSubjects(List.of(
                buildSubject(SUBJ_EELEC_MAT_4, "Matemática", "MAT-ELEC-4", 4, ORIENTATION_ELECTRICISTA_ID, 5),
                buildSubject(SUBJ_EELEC_FISICA_4, "Física", "FIS-ELEC-4", 4, ORIENTATION_ELECTRICISTA_ID, 4),
                buildSubject(SUBJ_EELEC_LENGUA_4, "Lengua y Literatura", "LEN-ELEC-4", 4, ORIENTATION_ELECTRICISTA_ID, 3),
                buildSubject(SUBJ_EELEC_ELEC_4, "Electrotecnia", "ETEC-ELEC-4", 4, ORIENTATION_ELECTRICISTA_ID, 5),
                buildSubject(SUBJ_EELEC_INST_4, "Instalaciones Eléctricas", "INST-ELEC-4", 4, ORIENTATION_ELECTRICISTA_ID, 4),
                buildSubject(SUBJ_EELEC_DIBUJO_4, "Dibujo Técnico", "DT-ELEC-4", 4, ORIENTATION_ELECTRICISTA_ID, 3),
                buildSubject(SUBJ_EELEC_ED_FIS_4, "Educación Física", "EDF-ELEC-4", 4, ORIENTATION_ELECTRICISTA_ID, 2),

                buildSubject(SUBJ_EELEC_MAT_5, "Matemática", "MAT-ELEC-5", 5, ORIENTATION_ELECTRICISTA_ID, 5),
                buildSubject(SUBJ_EELEC_FISICA_5, "Física Aplicada", "FIS-ELEC-5", 5, ORIENTATION_ELECTRICISTA_ID, 4),
                buildSubject(SUBJ_EELEC_ELEC_5, "Electrónica", "ELEC-ELEC-5", 5, ORIENTATION_ELECTRICISTA_ID, 5),
                buildSubject(SUBJ_EELEC_MAQUINAS_5, "Máquinas Eléctricas", "MAQ-ELEC-5", 5, ORIENTATION_ELECTRICISTA_ID, 4),
                buildSubject(SUBJ_EELEC_INST_5, "Instalaciones de Alta Tensión", "IAT-ELEC-5", 5, ORIENTATION_ELECTRICISTA_ID, 4),
                buildSubject(SUBJ_EELEC_ED_FIS_5, "Educación Física", "EDF-ELEC-5", 5, ORIENTATION_ELECTRICISTA_ID, 2),

                buildSubject(SUBJ_EELEC_MAT_6, "Matemática", "MAT-ELEC-6", 6, ORIENTATION_ELECTRICISTA_ID, 4),
                buildSubject(SUBJ_EELEC_ELEC_6, "Electrónica Industrial", "EI-ELEC-6", 6, ORIENTATION_ELECTRICISTA_ID, 5),
                buildSubject(SUBJ_EELEC_MAQUINAS_6, "Máquinas y Accionamientos", "MA-ELEC-6", 6, ORIENTATION_ELECTRICISTA_ID, 5),
                buildSubject(SUBJ_EELEC_PROYECTO_6, "Proyecto Tecnológico", "PT-ELEC-6", 6, ORIENTATION_ELECTRICISTA_ID, 4),
                buildSubject(SUBJ_EELEC_LEGISL_6, "Legislación Laboral", "LL-ELEC-6", 6, ORIENTATION_ELECTRICISTA_ID, 2),
                buildSubject(SUBJ_EELEC_ED_FIS_6, "Educación Física", "EDF-ELEC-6", 6, ORIENTATION_ELECTRICISTA_ID, 2),

                buildSubject(SUBJ_EELEC_PROYECTO_7, "Proyecto Final Integrador", "PFI-ELEC-7", 7, ORIENTATION_ELECTRICISTA_ID, 6),
                buildSubject(SUBJ_EELEC_ELEC_7, "Sistemas Eléctricos Avanzados", "SEA-ELEC-7", 7, ORIENTATION_ELECTRICISTA_ID, 5),
                buildSubject(SUBJ_EELEC_GESTION_7, "Gestión Empresarial", "GE-ELEC-7", 7, ORIENTATION_ELECTRICISTA_ID, 3),
                buildSubject(SUBJ_EELEC_SEGURIDAD_7, "Seguridad e Higiene", "SH-ELEC-7", 7, ORIENTATION_ELECTRICISTA_ID, 3)
        ));

        // --- Ciclo Orientado 4°-7° — Técnico Electromecánico ---
        count += saveSubjects(List.of(
                buildSubject(SUBJ_EMEC_MAT_4, "Matemática", "MAT-EMEC-4", 4, ORIENTATION_ELECTROMECANICO_ID, 5),
                buildSubject(SUBJ_EMEC_FISICA_4, "Física", "FIS-EMEC-4", 4, ORIENTATION_ELECTROMECANICO_ID, 4),
                buildSubject(SUBJ_EMEC_LENGUA_4, "Lengua y Literatura", "LEN-EMEC-4", 4, ORIENTATION_ELECTROMECANICO_ID, 3),
                buildSubject(SUBJ_EMEC_MECANICA_4, "Mecánica Técnica", "MEC-EMEC-4", 4, ORIENTATION_ELECTROMECANICO_ID, 5),
                buildSubject(SUBJ_EMEC_TERMOT_4, "Termodinámica", "TERM-EMEC-4", 4, ORIENTATION_ELECTROMECANICO_ID, 4),
                buildSubject(SUBJ_EMEC_DIBUJO_4, "Dibujo Técnico", "DT-EMEC-4", 4, ORIENTATION_ELECTROMECANICO_ID, 3),
                buildSubject(SUBJ_EMEC_ED_FIS_4, "Educación Física", "EDF-EMEC-4", 4, ORIENTATION_ELECTROMECANICO_ID, 2),

                buildSubject(SUBJ_EMEC_MAT_5, "Matemática", "MAT-EMEC-5", 5, ORIENTATION_ELECTROMECANICO_ID, 5),
                buildSubject(SUBJ_EMEC_FISICA_5, "Física Aplicada", "FIS-EMEC-5", 5, ORIENTATION_ELECTROMECANICO_ID, 4),
                buildSubject(SUBJ_EMEC_MECANICA_5, "Mecánica de Fluidos", "MF-EMEC-5", 5, ORIENTATION_ELECTROMECANICO_ID, 4),
                buildSubject(SUBJ_EMEC_MAQUINAS_5, "Máquinas Térmicas", "MT-EMEC-5", 5, ORIENTATION_ELECTROMECANICO_ID, 5),
                buildSubject(SUBJ_EMEC_HIDRO_5, "Hidráulica y Neumática", "HN-EMEC-5", 5, ORIENTATION_ELECTROMECANICO_ID, 4),
                buildSubject(SUBJ_EMEC_ED_FIS_5, "Educación Física", "EDF-EMEC-5", 5, ORIENTATION_ELECTROMECANICO_ID, 2),

                buildSubject(SUBJ_EMEC_MAT_6, "Matemática", "MAT-EMEC-6", 6, ORIENTATION_ELECTROMECANICO_ID, 4),
                buildSubject(SUBJ_EMEC_MAQUINAS_6, "Máquinas y Equipos Industriales", "MEI-EMEC-6", 6, ORIENTATION_ELECTROMECANICO_ID, 5),
                buildSubject(SUBJ_EMEC_MECANICA_6, "Mecánica Industrial", "MI-EMEC-6", 6, ORIENTATION_ELECTROMECANICO_ID, 5),
                buildSubject(SUBJ_EMEC_PROYECTO_6, "Proyecto Tecnológico", "PT-EMEC-6", 6, ORIENTATION_ELECTROMECANICO_ID, 4),
                buildSubject(SUBJ_EMEC_LEGISL_6, "Legislación Laboral", "LL-EMEC-6", 6, ORIENTATION_ELECTROMECANICO_ID, 2),
                buildSubject(SUBJ_EMEC_ED_FIS_6, "Educación Física", "EDF-EMEC-6", 6, ORIENTATION_ELECTROMECANICO_ID, 2),

                buildSubject(SUBJ_EMEC_PROYECTO_7, "Proyecto Final Integrador", "PFI-EMEC-7", 7, ORIENTATION_ELECTROMECANICO_ID, 6),
                buildSubject(SUBJ_EMEC_MAQUINAS_7, "Sistemas Mecatrónicos", "SM-EMEC-7", 7, ORIENTATION_ELECTROMECANICO_ID, 5),
                buildSubject(SUBJ_EMEC_GESTION_7, "Gestión Empresarial", "GE-EMEC-7", 7, ORIENTATION_ELECTROMECANICO_ID, 3),
                buildSubject(SUBJ_EMEC_SEGURIDAD_7, "Seguridad e Higiene", "SH-EMEC-7", 7, ORIENTATION_ELECTROMECANICO_ID, 3)
        ));

        log.info("✓ Created {} subjects", count);
    }

    private int saveSubjects(List<SubjectEntity> subjects) {
        subjectRepository.saveAll(subjects);
        subjects.forEach(s -> log.info("  ✓ Subject: {} ({})", s.getName(), s.getCode()));
        return subjects.size();
    }

    private SubjectEntity buildSubject(UUID id, String name, String code,
                                       int yearLevel, UUID orientationId, int weeklyHours) {
        SubjectEntity e = new SubjectEntity();
        e.setSubjectId(id);
        e.setName(name);
        e.setCode(code);
        e.setYearLevel(yearLevel);
        e.setOrientationId(orientationId);
        e.setMandatory(true);
        e.setWeeklyHours(weeklyHours);
        e.setActive(true);
        e.setCreatedAt(LocalDateTime.now());
        e.setUpdatedAt(LocalDateTime.now());
        return e;
    }

    // =========================================================================
    // GRADE LEVELS — 2025 (año activo)
    // 1°-2°: A/B/C/D, 3°-6°: A/B/C, 7°: A/B
    // =========================================================================
    private void seedGradeLevels() {
        log.info("Seeding grade levels for 2025...");

        List<GradeLevelData> gradeLevels = new ArrayList<>();

        // 1° año — A/B/C/D (sin orientación)
        gradeLevels.add(new GradeLevelData(GL_2025_1A, ACADEMIC_YEAR_2025_ID, 1, "A", null));
        gradeLevels.add(new GradeLevelData(GL_2025_1B, ACADEMIC_YEAR_2025_ID, 1, "B", null));
        gradeLevels.add(new GradeLevelData(GL_2025_1C, ACADEMIC_YEAR_2025_ID, 1, "C", null));
        gradeLevels.add(new GradeLevelData(GL_2025_1D, ACADEMIC_YEAR_2025_ID, 1, "D", null));

        // 2° año — A/B/C/D (sin orientación)
        gradeLevels.add(new GradeLevelData(GL_2025_2A, ACADEMIC_YEAR_2025_ID, 2, "A", null));
        gradeLevels.add(new GradeLevelData(GL_2025_2B, ACADEMIC_YEAR_2025_ID, 2, "B", null));
        gradeLevels.add(new GradeLevelData(GL_2025_2C, ACADEMIC_YEAR_2025_ID, 2, "C", null));
        gradeLevels.add(new GradeLevelData(GL_2025_2D, ACADEMIC_YEAR_2025_ID, 2, "D", null));

        // 3° año — A/B/C (sin orientación)
        gradeLevels.add(new GradeLevelData(GL_2025_3A, ACADEMIC_YEAR_2025_ID, 3, "A", null));
        gradeLevels.add(new GradeLevelData(GL_2025_3B, ACADEMIC_YEAR_2025_ID, 3, "B", null));
        gradeLevels.add(new GradeLevelData(GL_2025_3C, ACADEMIC_YEAR_2025_ID, 3, "C", null));

        // 4° año — A/B Electricista, C Electromecánico
        gradeLevels.add(new GradeLevelData(GL_2025_4A, ACADEMIC_YEAR_2025_ID, 4, "A", ORIENTATION_ELECTRICISTA_ID));
        gradeLevels.add(new GradeLevelData(GL_2025_4B, ACADEMIC_YEAR_2025_ID, 4, "B", ORIENTATION_ELECTRICISTA_ID));
        gradeLevels.add(new GradeLevelData(GL_2025_4C, ACADEMIC_YEAR_2025_ID, 4, "C", ORIENTATION_ELECTROMECANICO_ID));

        // 5° año — A/B Electricista, C Electromecánico
        gradeLevels.add(new GradeLevelData(GL_2025_5A, ACADEMIC_YEAR_2025_ID, 5, "A", ORIENTATION_ELECTRICISTA_ID));
        gradeLevels.add(new GradeLevelData(GL_2025_5B, ACADEMIC_YEAR_2025_ID, 5, "B", ORIENTATION_ELECTRICISTA_ID));
        gradeLevels.add(new GradeLevelData(GL_2025_5C, ACADEMIC_YEAR_2025_ID, 5, "C", ORIENTATION_ELECTROMECANICO_ID));

        // 6° año — A/B Electricista, C Electromecánico
        gradeLevels.add(new GradeLevelData(GL_2025_6A, ACADEMIC_YEAR_2025_ID, 6, "A", ORIENTATION_ELECTRICISTA_ID));
        gradeLevels.add(new GradeLevelData(GL_2025_6B, ACADEMIC_YEAR_2025_ID, 6, "B", ORIENTATION_ELECTRICISTA_ID));
        gradeLevels.add(new GradeLevelData(GL_2025_6C, ACADEMIC_YEAR_2025_ID, 6, "C", ORIENTATION_ELECTROMECANICO_ID));

        // 7° año — A Electricista, B Electromecánico
        gradeLevels.add(new GradeLevelData(GL_2025_7A, ACADEMIC_YEAR_2025_ID, 7, "A", ORIENTATION_ELECTRICISTA_ID));
        gradeLevels.add(new GradeLevelData(GL_2025_7B, ACADEMIC_YEAR_2025_ID, 7, "B", ORIENTATION_ELECTROMECANICO_ID));

        for (GradeLevelData data : gradeLevels) {
            gradeLevelRepository.save(buildGradeLevel(data));
            log.info("  ✓ GradeLevel: {}°{} ({})",
                    data.yearLevel(), data.division(),
                    data.orientationId() == null ? "Ciclo Básico" :
                            data.orientationId().equals(ORIENTATION_ELECTRICISTA_ID)
                                    ? "Electricista" : "Electromecánico");
        }

        log.info("✓ Created {} grade levels for 2025", gradeLevels.size());
    }

    private GradeLevelEntity buildGradeLevel(GradeLevelData data) {
        GradeLevelEntity e = new GradeLevelEntity();
        e.setGradeLevelId(data.id());
        e.setAcademicYearId(data.academicYearId());
        e.setYearLevel(data.yearLevel());
        e.setDivision(data.division());
        e.setOrientationId(data.orientationId());
        e.setShift("MORNING");
        e.setMaxStudents(35);
        e.setActive(true);
        e.setCreatedAt(LocalDateTime.now());
        e.setUpdatedAt(LocalDateTime.now());
        return e;
    }

    // =========================================================================
    // QUALIFICATION REGISTRIES
    // =========================================================================
    private void seedQualificationRegistries() {
        log.info("Seeding qualification registries...");

        registryRepository.save(buildRegistry(
                REGISTRY_2024_ID, ACADEMIC_YEAR_2024_ID, "REG-2024-000001",
                1, 500, "CLOSED"
        ));
        log.info("  ✓ Registry: REG-2024-000001 (CLOSED)");

        registryRepository.save(buildRegistry(
                REGISTRY_2025_ID, ACADEMIC_YEAR_2025_ID, "REG-2025-000001",
                1, 500, "ACTIVE"
        ));
        log.info("  ✓ Registry: REG-2025-000001 (ACTIVE)");

        log.info("✓ Created 2 qualification registries");
    }

    private QualificationRegistryEntity buildRegistry(UUID id, UUID academicYearId,
                                                      String number, int startFolio,
                                                      int endFolio, String status) {
        QualificationRegistryEntity e = new QualificationRegistryEntity();
        e.setRegistryId(id);
        e.setAcademicYearId(academicYearId);
        e.setRegistryNumber(number);
        e.setStartFolio(startFolio);
        e.setEndFolio(endFolio);
        e.setCurrentFolio(startFolio);
        e.setMaxFolios(500);
        e.setStatus(status);
        e.setCreatedAt(LocalDateTime.now());
        return e;
    }

    // =========================================================================
    // STATISTICS
    // =========================================================================
    private void logStatistics() {
        log.info("Academic Statistics:");
        log.info("  - Academic years:             {}", academicYearRepository.count());
        log.info("  - Orientations:               {}", orientationRepository.count());
        log.info("  - Subjects:                   {}", subjectRepository.count());
        log.info("  - Grade levels:               {}", gradeLevelRepository.count());
        log.info("  - Qualification registries:   {}", registryRepository.count());
    }

    // =========================================================================
    // HELPERS
    // =========================================================================
    private record GradeLevelData(
            UUID id,
            UUID academicYearId,
            int yearLevel,
            String division,
            UUID orientationId
    ) {}
}
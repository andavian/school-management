package org.school.management.course.infrastructure.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.infra.seeder.AcademicDataSeeder;
import org.school.management.course.domain.valueobject.CourseStatus;
import org.school.management.course.infrastructure.persistence.entity.CourseSubjectEntity;
import org.school.management.course.infrastructure.persistence.repository.CourseSubjectJpaRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Seeder para el módulo de Cursos del IPET 132.
 *
 * Siembra CourseSubject (asignación materia-curso) para el año 2025.
 * No siembra StudentCourseSubject — esas inscripciones se generan
 * al crear las matrículas de cada alumno.
 *
 * Depende de AcademicDataSeeder (@Order(5)) — referencia sus UUIDs fijos.
 *
 * @Order(6) — ejecuta inmediatamente después de AcademicDataSeeder
 */
@Component
@Profile("dev")
@Order(6)
@RequiredArgsConstructor
@Slf4j
public class CourseDataSeeder implements ApplicationRunner {

    private final CourseSubjectJpaRepository courseSubjectRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("=".repeat(80));
        log.info("Starting Course Data Seeder...");
        log.info("=".repeat(80));

        if (courseSubjectRepository.count() > 0) {
            log.info("Course data already exists. Skipping seeder.");
            return;
        }

        try {
            seedCourseSubjects();
            log.info("=".repeat(80));
            log.info("Course Data Seeder completed successfully!");
            logStatistics();
            log.info("=".repeat(80));
        } catch (Exception e) {
            log.error("Error seeding course data", e);
            throw e;
        }
    }

    // =========================================================================
    // COURSE SUBJECTS 2025
    // Un CourseSubject por cada (grade_level, subject) del año 2025.
    // Sin teacher asignado — se asignan luego via API.
    // =========================================================================

    private void seedCourseSubjects() {
        log.info("Seeding course subjects for 2025...");

        List<CourseSubjectEntity> entities = new ArrayList<>();

        // --- 1° año: A/B/C/D — mismo plan (ciclo básico) ---
        for (UUID gradeLevelId : List.of(
                AcademicDataSeeder.GL_2025_1A, AcademicDataSeeder.GL_2025_1B,
                AcademicDataSeeder.GL_2025_1C, AcademicDataSeeder.GL_2025_1D)) {
            entities.addAll(buildCourseSubjectsForGradeLevel(gradeLevelId, List.of(
                    AcademicDataSeeder.SUBJ_MATEMATICA_1,
                    AcademicDataSeeder.SUBJ_LENGUA_1,
                    AcademicDataSeeder.SUBJ_HISTORIA_1,
                    AcademicDataSeeder.SUBJ_GEOGRAFIA_1,
                    AcademicDataSeeder.SUBJ_FISICA_1,
                    AcademicDataSeeder.SUBJ_QUIMICA_1,
                    AcademicDataSeeder.SUBJ_ED_FISICA_1,
                    AcademicDataSeeder.SUBJ_FORMACION_C_1,
                    AcademicDataSeeder.SUBJ_TECNOLOGIA_1,
                    AcademicDataSeeder.SUBJ_INGLES_1
            )));
        }

        // --- 2° año: A/B/C/D ---
        for (UUID gradeLevelId : List.of(
                AcademicDataSeeder.GL_2025_2A, AcademicDataSeeder.GL_2025_2B,
                AcademicDataSeeder.GL_2025_2C, AcademicDataSeeder.GL_2025_2D)) {
            entities.addAll(buildCourseSubjectsForGradeLevel(gradeLevelId, List.of(
                    AcademicDataSeeder.SUBJ_MATEMATICA_2,
                    AcademicDataSeeder.SUBJ_LENGUA_2,
                    AcademicDataSeeder.SUBJ_HISTORIA_2,
                    AcademicDataSeeder.SUBJ_GEOGRAFIA_2,
                    AcademicDataSeeder.SUBJ_FISICA_2,
                    AcademicDataSeeder.SUBJ_QUIMICA_2,
                    AcademicDataSeeder.SUBJ_ED_FISICA_2,
                    AcademicDataSeeder.SUBJ_FORMACION_C_2,
                    AcademicDataSeeder.SUBJ_TECNOLOGIA_2,
                    AcademicDataSeeder.SUBJ_INGLES_2
            )));
        }

        // --- 3° año: A/B/C ---
        for (UUID gradeLevelId : List.of(
                AcademicDataSeeder.GL_2025_3A, AcademicDataSeeder.GL_2025_3B,
                AcademicDataSeeder.GL_2025_3C)) {
            entities.addAll(buildCourseSubjectsForGradeLevel(gradeLevelId, List.of(
                    AcademicDataSeeder.SUBJ_MATEMATICA_3,
                    AcademicDataSeeder.SUBJ_LENGUA_3,
                    AcademicDataSeeder.SUBJ_HISTORIA_3,
                    AcademicDataSeeder.SUBJ_FISICA_3,
                    AcademicDataSeeder.SUBJ_QUIMICA_3,
                    AcademicDataSeeder.SUBJ_ED_FISICA_3,
                    AcademicDataSeeder.SUBJ_FORMACION_C_3,
                    AcademicDataSeeder.SUBJ_TECNOLOGIA_3,
                    AcademicDataSeeder.SUBJ_INGLES_3,
                    AcademicDataSeeder.SUBJ_DIBUJO_TEC_3
            )));
        }

        // --- 4° año: A/B Electricista, C Electromecánico ---
        for (UUID gradeLevelId : List.of(
                AcademicDataSeeder.GL_2025_4A, AcademicDataSeeder.GL_2025_4B)) {
            entities.addAll(buildCourseSubjectsForGradeLevel(gradeLevelId, List.of(
                    AcademicDataSeeder.SUBJ_EELEC_MAT_4,
                    AcademicDataSeeder.SUBJ_EELEC_FISICA_4,
                    AcademicDataSeeder.SUBJ_EELEC_LENGUA_4,
                    AcademicDataSeeder.SUBJ_EELEC_ELEC_4,
                    AcademicDataSeeder.SUBJ_EELEC_INST_4,
                    AcademicDataSeeder.SUBJ_EELEC_DIBUJO_4,
                    AcademicDataSeeder.SUBJ_EELEC_ED_FIS_4
            )));
        }
        entities.addAll(buildCourseSubjectsForGradeLevel(AcademicDataSeeder.GL_2025_4C, List.of(
                AcademicDataSeeder.SUBJ_EMEC_MAT_4,
                AcademicDataSeeder.SUBJ_EMEC_FISICA_4,
                AcademicDataSeeder.SUBJ_EMEC_LENGUA_4,
                AcademicDataSeeder.SUBJ_EMEC_MECANICA_4,
                AcademicDataSeeder.SUBJ_EMEC_TERMOT_4,
                AcademicDataSeeder.SUBJ_EMEC_DIBUJO_4,
                AcademicDataSeeder.SUBJ_EMEC_ED_FIS_4
        )));

        // --- 5° año: A/B Electricista, C Electromecánico ---
        for (UUID gradeLevelId : List.of(
                AcademicDataSeeder.GL_2025_5A, AcademicDataSeeder.GL_2025_5B)) {
            entities.addAll(buildCourseSubjectsForGradeLevel(gradeLevelId, List.of(
                    AcademicDataSeeder.SUBJ_EELEC_MAT_5,
                    AcademicDataSeeder.SUBJ_EELEC_FISICA_5,
                    AcademicDataSeeder.SUBJ_EELEC_ELEC_5,
                    AcademicDataSeeder.SUBJ_EELEC_MAQUINAS_5,
                    AcademicDataSeeder.SUBJ_EELEC_INST_5,
                    AcademicDataSeeder.SUBJ_EELEC_ED_FIS_5
            )));
        }
        entities.addAll(buildCourseSubjectsForGradeLevel(AcademicDataSeeder.GL_2025_5C, List.of(
                AcademicDataSeeder.SUBJ_EMEC_MAT_5,
                AcademicDataSeeder.SUBJ_EMEC_FISICA_5,
                AcademicDataSeeder.SUBJ_EMEC_MECANICA_5,
                AcademicDataSeeder.SUBJ_EMEC_MAQUINAS_5,
                AcademicDataSeeder.SUBJ_EMEC_HIDRO_5,
                AcademicDataSeeder.SUBJ_EMEC_ED_FIS_5
        )));

        // --- 6° año: A/B Electricista, C Electromecánico ---
        for (UUID gradeLevelId : List.of(
                AcademicDataSeeder.GL_2025_6A, AcademicDataSeeder.GL_2025_6B)) {
            entities.addAll(buildCourseSubjectsForGradeLevel(gradeLevelId, List.of(
                    AcademicDataSeeder.SUBJ_EELEC_MAT_6,
                    AcademicDataSeeder.SUBJ_EELEC_ELEC_6,
                    AcademicDataSeeder.SUBJ_EELEC_MAQUINAS_6,
                    AcademicDataSeeder.SUBJ_EELEC_PROYECTO_6,
                    AcademicDataSeeder.SUBJ_EELEC_LEGISL_6,
                    AcademicDataSeeder.SUBJ_EELEC_ED_FIS_6
            )));
        }
        entities.addAll(buildCourseSubjectsForGradeLevel(AcademicDataSeeder.GL_2025_6C, List.of(
                AcademicDataSeeder.SUBJ_EMEC_MAT_6,
                AcademicDataSeeder.SUBJ_EMEC_MAQUINAS_6,
                AcademicDataSeeder.SUBJ_EMEC_MECANICA_6,
                AcademicDataSeeder.SUBJ_EMEC_PROYECTO_6,
                AcademicDataSeeder.SUBJ_EMEC_LEGISL_6,
                AcademicDataSeeder.SUBJ_EMEC_ED_FIS_6
        )));

        // --- 7° año: A Electricista, B Electromecánico ---
        entities.addAll(buildCourseSubjectsForGradeLevel(AcademicDataSeeder.GL_2025_7A, List.of(
                AcademicDataSeeder.SUBJ_EELEC_PROYECTO_7,
                AcademicDataSeeder.SUBJ_EELEC_ELEC_7,
                AcademicDataSeeder.SUBJ_EELEC_GESTION_7,
                AcademicDataSeeder.SUBJ_EELEC_SEGURIDAD_7
        )));
        entities.addAll(buildCourseSubjectsForGradeLevel(AcademicDataSeeder.GL_2025_7B, List.of(
                AcademicDataSeeder.SUBJ_EMEC_PROYECTO_7,
                AcademicDataSeeder.SUBJ_EMEC_MAQUINAS_7,
                AcademicDataSeeder.SUBJ_EMEC_GESTION_7,
                AcademicDataSeeder.SUBJ_EMEC_SEGURIDAD_7
        )));

        courseSubjectRepository.saveAll(entities);
        log.info("✓ Created {} course subjects", entities.size());
    }

    private List<CourseSubjectEntity> buildCourseSubjectsForGradeLevel(
            UUID gradeLevelId, List<UUID> subjectIds) {
        List<CourseSubjectEntity> result = new ArrayList<>();
        for (UUID subjectId : subjectIds) {
            result.add(buildCourseSubject(gradeLevelId, subjectId));
        }
        return result;
    }

    private CourseSubjectEntity buildCourseSubject(UUID gradeLevelId, UUID subjectId) {
        CourseSubjectEntity e = new CourseSubjectEntity();
        e.setCourseSubjectId(UUID.randomUUID());
        e.setGradeLevelId(gradeLevelId);
        e.setSubjectId(subjectId);
        e.setTeacherId(null);                           // sin docente asignado inicialmente
        e.setAcademicYearId(AcademicDataSeeder.ACADEMIC_YEAR_2025_ID);
        e.setMinPassingGrade(BigDecimal.valueOf(6.00));
        e.setStatus(CourseStatus.ACTIVE);
        e.setCreatedAt(LocalDateTime.now());
        e.setUpdatedAt(LocalDateTime.now());
        return e;
    }

    private void logStatistics() {
        long total = courseSubjectRepository.count();
        log.info("Course Statistics:");
        log.info("  - Course subjects total: {}", total);
    }
}
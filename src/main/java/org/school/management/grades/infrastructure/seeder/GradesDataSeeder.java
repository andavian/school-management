package org.school.management.grades.infrastructure.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.grades.infrastructure.persistence.entity.EvaluationTypeEntity;
import org.school.management.grades.infrastructure.persistence.repository.EvaluationTypeJpaRepository;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Seeder para el módulo de Calificaciones.
 * Puebla los tipos de evaluación iniciales para el IPET 132.
 *
 * @Order(10) — ejecuta después de seeders de academic/ y students/
 */
@Component
@Profile("dev")
@Order(10)
@RequiredArgsConstructor
@Slf4j
public class GradesDataSeeder implements ApplicationRunner {

    private final EvaluationTypeJpaRepository evaluationTypeJpaRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("=".repeat(80));
        log.info("Starting Grades Data Seeder...");
        log.info("=".repeat(80));

        if (evaluationTypeJpaRepository.count() > 0) {
            log.info("Grades data already exists. Skipping seeder.");
            return;
        }

        try {
            seedEvaluationTypes();
            log.info("=".repeat(80));
            log.info("Grades Data Seeder completed successfully!");
            logStatistics();
            log.info("=".repeat(80));
        } catch (Exception e) {
            log.error("Error seeding grades data", e);
            throw e;
        }
    }

    private void seedEvaluationTypes() {
        log.info("Seeding evaluation types...");

        List<EvaluationTypeData> types = List.of(
                new EvaluationTypeData(
                        "550e8400-e29b-41d4-a716-446655440001",
                        "Parcial",
                        "PARCIAL",
                        "Evaluación parcial escrita",
                        new BigDecimal("40.00")
                ),
                new EvaluationTypeData(
                        "550e8400-e29b-41d4-a716-446655440002",
                        "Trabajo Práctico",
                        "TRABAJO_PRACTICO",
                        "Trabajo práctico individual o grupal",
                        new BigDecimal("30.00")
                ),
                new EvaluationTypeData(
                        "550e8400-e29b-41d4-a716-446655440003",
                        "Coloquio",
                        "COLOQUIO",
                        "Instancia de recuperación — diciembre o febrero",
                        new BigDecimal("100.00")
                ),
                new EvaluationTypeData(
                        "550e8400-e29b-41d4-a716-446655440004",
                        "Examen Previo",
                        "EXAMEN_PREVIO",
                        "Examen de materia previa — instancia final",
                        new BigDecimal("100.00")
                ),
                new EvaluationTypeData(
                        "550e8400-e29b-41d4-a716-446655440005",
                        "Evaluación Continua",
                        "EVALUACION_CONTINUA",
                        "Evaluación continua del proceso de aprendizaje",
                        new BigDecimal("30.00")
                )
        );

        for (EvaluationTypeData data : types) {
            EvaluationTypeEntity entity = buildEvaluationTypeEntity(data);
            evaluationTypeJpaRepository.save(entity);
            log.info("  ✓ EvaluationType: {} ({})", data.name(), data.code());
        }

        log.info("✓ Created {} evaluation types", types.size());
    }

    private EvaluationTypeEntity buildEvaluationTypeEntity(EvaluationTypeData data) {
        EvaluationTypeEntity entity = new EvaluationTypeEntity();
        entity.setEvaluationTypeId(UUID.fromString(data.id()));
        entity.setName(data.name());
        entity.setCode(data.code());
        entity.setDescription(data.description());
        entity.setWeightPercentage(data.weightPercentage());
        entity.setActive(true);
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    private void logStatistics() {
        long total = evaluationTypeJpaRepository.count();
        long active = evaluationTypeJpaRepository.findByIsActiveTrue().size();

        log.info("Grades Statistics:");
        log.info("  - Evaluation types total:  {}", total);
        log.info("  - Evaluation types active: {}", active);
    }

    /**
     * Record helper para datos de tipos de evaluación
     */
    private record EvaluationTypeData(
            String id,
            String name,
            String code,
            String description,
            BigDecimal weightPercentage
    ) {}
}

package org.school.management.academic.infra.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.infra.persistence.entity.EvaluationPeriodEntity;
import org.school.management.academic.infra.persistence.repository.EvaluationPeriodJpaRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Seeder para períodos de evaluación del IPET 132.
 * Siembra 2 períodos cuatrimestrales para el año lectivo 2025.
 *
 * @Order(5) — ejecuta después de AcademicDataSeeder (@Order(4)).
 * Usa los UUIDs públicos de AcademicDataSeeder para el año académico.
 */
@Component
@Profile("dev")
@Order(5)
@RequiredArgsConstructor
@Slf4j
public class EvaluationPeriodSeeder implements ApplicationRunner {

    private final EvaluationPeriodJpaRepository evaluationPeriodRepository;

    // =========================================================================
    // UUIDs FIJOS — Evaluation Periods 2025
    // =========================================================================
    public static final UUID PERIOD_2025_1C_ID = UUID.fromString("e0000000-0000-0000-0000-000000000001");
    public static final UUID PERIOD_2025_2C_ID = UUID.fromString("e0000000-0000-0000-0000-000000000002");

    // Referencia al año académico 2025 definido en AcademicDataSeeder
    public static final UUID ACADEMIC_YEAR_2025_ID = AcademicDataSeeder.ACADEMIC_YEAR_2025_ID;

    @Override
    public void run(ApplicationArguments args) {
        log.info("=".repeat(80));
        log.info("Starting Evaluation Period Seeder...");
        log.info("=".repeat(80));

        if (evaluationPeriodRepository.count() > 0) {
            log.info("Evaluation periods already exist. Skipping seeder.");
            return;
        }

        try {
            seedEvaluationPeriods();
            log.info("=".repeat(80));
            log.info("Evaluation Period Seeder completed successfully!");
            log.info("  - Periods created: {}", evaluationPeriodRepository.count());
            log.info("=".repeat(80));
        } catch (Exception e) {
            log.error("Error seeding evaluation periods", e);
            throw e;
        }
    }

    private void seedEvaluationPeriods() {
        LocalDate today = LocalDate.now();

        // Período 1: 1er Cuatrimestre (marzo - julio)
        EvaluationPeriodEntity period1 = buildPeriod(
                PERIOD_2025_1C_ID,
                ACADEMIC_YEAR_2025_ID,
                1,
                "1er Cuatrimestre 2025",
                LocalDate.of(2025, 3, 4),
                LocalDate.of(2025, 7, 11),
                today.isAfter(LocalDate.of(2025, 3, 3)) && today.isBefore(LocalDate.of(2025, 7, 12)),
                "ACTIVE"
        );
        evaluationPeriodRepository.save(period1);
        log.info("  ✓ Period: 1er Cuatrimestre 2025 (ACTIVE)");

        // Período 2: 2do Cuatrimestre (agosto - diciembre)
        EvaluationPeriodEntity period2 = buildPeriod(
                PERIOD_2025_2C_ID,
                ACADEMIC_YEAR_2025_ID,
                2,
                "2do Cuatrimestre 2025",
                LocalDate.of(2025, 8, 4),
                LocalDate.of(2025, 12, 12),
                !today.isBefore(LocalDate.of(2025, 8, 4)),
                today.isBefore(LocalDate.of(2025, 8, 4)) ? "PENDING" : "ACTIVE"
        );
        evaluationPeriodRepository.save(period2);
        log.info("  ✓ Period: 2do Cuatrimestre 2025 ({})",
                today.isBefore(LocalDate.of(2025, 8, 4)) ? "PENDING" : "ACTIVE");
    }

    private EvaluationPeriodEntity buildPeriod(
            UUID periodId, UUID academicYearId, int number,
            String name, LocalDate start, LocalDate end,
            boolean isCurrent, String status) {

        EvaluationPeriodEntity e = new EvaluationPeriodEntity();
        e.setPeriodId(periodId);
        e.setAcademicYearId(academicYearId);
        e.setPeriodNumber(number);
        e.setName(name);
        e.setStartDate(start);
        e.setEndDate(end);
        e.setIsCurrent(isCurrent);
        e.setStatus(status);
        e.setCreatedAt(LocalDateTime.now());
        e.setUpdatedAt(LocalDateTime.now());
        return e;
    }
}
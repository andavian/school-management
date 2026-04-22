package org.school.management.students.records.infrastructure.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.infra.persistence.repository.UserJpaRepository;
import org.school.management.students.personal.infrastructure.seeder.StudentAndParentDataSeeder;
import org.school.management.students.records.domain.valueobject.DocumentStatus;
import org.school.management.students.records.infrastructure.persistence.entity.RecordDocumentEntity;
import org.school.management.students.records.infrastructure.persistence.entity.StudentRecordEntity;
import org.school.management.students.records.infrastructure.persistence.repository.RecordDocumentJpaRepository;
import org.school.management.students.records.infrastructure.persistence.repository.StudentRecordJpaRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Seeder de documentos del legajo para el perfil dev del IPET 132.
 *
 * Siembra RecordDocuments de prueba para los 4 alumnos creados por
 * StudentAndParentDataSeeder. Los record_id se resuelven en runtime
 * buscando por student_id, ya que el seeder anterior los genera con
 * UUID random.
 *
 * Referencia los UUIDs fijos de:
 *   - StudentAndParentDataSeeder  — STUDENT_*_ID
 *   - DocumentTypeSeeder          — DOC_TYPE_*
 *
 * Escenarios de prueba por alumno:
 *   - Lucas Romero  (1°A): legajo COMPLETO — todos los obligatorios APPROVED
 *   - Sofía Torres  (1°A): legajo INCOMPLETO — algunos PENDING, falta salud
 *   - Martín Díaz   (4°A): legajo PARCIAL — obligatorios ok, opcionales PENDING
 *   - Ana Gómez     (4°C): legajo con documento REJECTED para testear flujo review
 *
 * @Order(9) — ejecuta después de StudentAndParentDataSeeder (@Order(8))
 */
@Component
@Profile("dev")
@Order(9)
@RequiredArgsConstructor
@Slf4j
public class RecordDocumentSeeder implements ApplicationRunner {

    private final RecordDocumentJpaRepository  documentRepository;
    private final StudentRecordJpaRepository   recordRepository;
    private final UserJpaRepository            userRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("=".repeat(80));
        log.info("Starting Record Document Seeder...");
        log.info("=".repeat(80));

        if (documentRepository.count() > 0) {
            log.info("Record document data already exists. Skipping seeder.");
            return;
        }

        try {
            UUID adminUserId = resolveAdminUserId();

            seedDocumentsForLucas(adminUserId);
            seedDocumentsForSofia(adminUserId);
            seedDocumentsForMartin(adminUserId);
            seedDocumentsForAna(adminUserId);

            log.info("=".repeat(80));
            log.info("Record Document Seeder completed successfully!");
            logStatistics();
            log.info("=".repeat(80));
        } catch (Exception e) {
            log.error("Error seeding record document data", e);
            throw e;
        }
    }

    // =========================================================================
    // Resolución de dependencias en runtime
    // =========================================================================

    /**
     * Los record_id fueron generados con UUID.randomUUID() en StudentAndParentDataSeeder,
     * por eso los resolvemos buscando por student_id en lugar de usar constantes.
     */
    private UUID resolveRecordId(UUID studentId) {
        return recordRepository.findByStudentId(studentId)
                .map(StudentRecordEntity::getRecordId)
                .orElseThrow(() -> new IllegalStateException(
                        "StudentRecord not found for studentId: " + studentId +
                                ". Run StudentAndParentDataSeeder first (@Order 8)."));
    }

    /**
     * Resuelve el UUID del admin por DNI — mismo patrón que StudentAndParentDataSeeder.
     */
    private UUID resolveAdminUserId() {
        return userRepository.findByDni("10000001")
                .map(u -> u.getUserId())
                .orElseThrow(() -> new IllegalStateException(
                        "Admin user (DNI 10000001) not found. Run auth migration first."));
    }

    // =========================================================================
    // Lucas Romero — legajo COMPLETO (escenario ideal para demos)
    // Todos los documentos obligatorios APPROVED + algunos opcionales
    // =========================================================================

    private void seedDocumentsForLucas(UUID adminUserId) {
        UUID recordId = resolveRecordId(StudentAndParentDataSeeder.STUDENT_1_ID);
        log.info("Seeding documents for Lucas Romero (record: {})...", recordId);

        List<RecordDocumentEntity> docs = new ArrayList<>();

        // Obligatorios — todos APPROVED
        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_DNI_FRONT,
                "DNI Frente — Lucas Romero",
                "records/lucas-romero/dni-frente.pdf",
                "https://storage.ipet132.edu.ar/records/lucas-romero/dni-frente.pdf",
                "application/pdf", 245_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2025, 3, 3), null, "RENAPER",
                adminUserId));

        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_DNI_BACK,
                "DNI Dorso — Lucas Romero",
                "records/lucas-romero/dni-dorso.pdf",
                "https://storage.ipet132.edu.ar/records/lucas-romero/dni-dorso.pdf",
                "application/pdf", 238_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2025, 3, 3), null, "RENAPER",
                adminUserId));

        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_BIRTH_CERTIFICATE,
                "Acta de Nacimiento — Lucas Romero",
                "records/lucas-romero/acta-nacimiento.pdf",
                "https://storage.ipet132.edu.ar/records/lucas-romero/acta-nacimiento.pdf",
                "application/pdf", 512_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2010, 3, 20), null, "Registro Civil de Córdoba",
                adminUserId));

        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_ID_PHOTO,
                "Foto Carnet — Lucas Romero",
                "records/lucas-romero/foto-carnet.jpg",
                "https://storage.ipet132.edu.ar/records/lucas-romero/foto-carnet.jpg",
                "image/jpeg", 85_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2025, 3, 3), null, null,
                adminUserId));

        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_STUDIES_CERTIFICATE,
                "Certificado de Estudios Primarios — Lucas Romero",
                "records/lucas-romero/certificado-estudios.pdf",
                "https://storage.ipet132.edu.ar/records/lucas-romero/certificado-estudios.pdf",
                "application/pdf", 380_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2024, 12, 15), null, "Escuela N° 123 Córdoba",
                adminUserId));

        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_HEALTH_CERTIFICATE,
                "Certificado de Aptitud Física — Lucas Romero",
                "records/lucas-romero/certificado-salud.pdf",
                "https://storage.ipet132.edu.ar/records/lucas-romero/certificado-salud.pdf",
                "application/pdf", 290_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2025, 3, 1),
                LocalDate.of(2026, 3, 1),
                "Dr. Pérez — Médico de Cabecera",
                adminUserId));

        // Opcionales — también APPROVED
        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_VACCINATION_CARD,
                "Carnet de Vacunación — Lucas Romero",
                "records/lucas-romero/carnet-vacunacion.pdf",
                "https://storage.ipet132.edu.ar/records/lucas-romero/carnet-vacunacion.pdf",
                "application/pdf", 410_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2025, 3, 3), null, "Ministerio de Salud",
                adminUserId));

        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_IMAGE_AUTHORIZATION,
                "Autorización de Imagen — Lucas Romero",
                "records/lucas-romero/autorizacion-imagen.pdf",
                "https://storage.ipet132.edu.ar/records/lucas-romero/autorizacion-imagen.pdf",
                "application/pdf", 180_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2025, 3, 3), null, null,
                adminUserId));

        documentRepository.saveAll(docs);
        log.info("  ✓ Lucas Romero: {} documentos (legajo COMPLETO)", docs.size());
    }

    // =========================================================================
    // Sofía Torres — legajo INCOMPLETO (falta certificado de salud)
    // Útil para testear el endpoint de completeness y alertas
    // =========================================================================

    private void seedDocumentsForSofia(UUID adminUserId) {
        UUID recordId = resolveRecordId(StudentAndParentDataSeeder.STUDENT_2_ID);
        log.info("Seeding documents for Sofía Torres (record: {})...", recordId);

        List<RecordDocumentEntity> docs = new ArrayList<>();

        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_DNI_FRONT,
                "DNI Frente — Sofía Torres",
                "records/sofia-torres/dni-frente.pdf",
                "https://storage.ipet132.edu.ar/records/sofia-torres/dni-frente.pdf",
                "application/pdf", 251_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2025, 3, 5), null, "RENAPER",
                adminUserId));

        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_DNI_BACK,
                "DNI Dorso — Sofía Torres",
                "records/sofia-torres/dni-dorso.pdf",
                "https://storage.ipet132.edu.ar/records/sofia-torres/dni-dorso.pdf",
                "application/pdf", 243_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2025, 3, 5), null, "RENAPER",
                adminUserId));

        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_BIRTH_CERTIFICATE,
                "Acta de Nacimiento — Sofía Torres",
                "records/sofia-torres/acta-nacimiento.pdf",
                "https://storage.ipet132.edu.ar/records/sofia-torres/acta-nacimiento.pdf",
                "application/pdf", 498_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2010, 7, 5), null, "Registro Civil de Córdoba",
                adminUserId));

        // Foto carnet PENDING — todavía sin revisar
        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_ID_PHOTO,
                "Foto Carnet — Sofía Torres",
                "records/sofia-torres/foto-carnet.jpg",
                "https://storage.ipet132.edu.ar/records/sofia-torres/foto-carnet.jpg",
                "image/jpeg", 92_000L,
                DocumentStatus.PENDING,
                LocalDate.of(2025, 3, 5), null, null,
                adminUserId));

        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_STUDIES_CERTIFICATE,
                "Certificado de Estudios Primarios — Sofía Torres",
                "records/sofia-torres/certificado-estudios.pdf",
                "https://storage.ipet132.edu.ar/records/sofia-torres/certificado-estudios.pdf",
                "application/pdf", 365_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2024, 12, 10), null, "Escuela N° 456 Córdoba",
                adminUserId));

        // HEALTH_CERTIFICATE ausente — legajo queda INCOMPLETO

        documentRepository.saveAll(docs);
        log.info("  ✓ Sofía Torres: {} documentos (legajo INCOMPLETO — falta certificado salud)",
                docs.size());
    }

    // =========================================================================
    // Martín Díaz — legajo PARCIAL (obligatorios ok, opcionales en PENDING)
    // =========================================================================

    private void seedDocumentsForMartin(UUID adminUserId) {
        UUID recordId = resolveRecordId(StudentAndParentDataSeeder.STUDENT_3_ID);
        log.info("Seeding documents for Martín Díaz (record: {})...", recordId);

        List<RecordDocumentEntity> docs = new ArrayList<>();

        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_DNI_FRONT,
                "DNI Frente — Martín Díaz",
                "records/martin-diaz/dni-frente.pdf",
                "https://storage.ipet132.edu.ar/records/martin-diaz/dni-frente.pdf",
                "application/pdf", 260_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2025, 3, 7), null, "RENAPER",
                adminUserId));

        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_DNI_BACK,
                "DNI Dorso — Martín Díaz",
                "records/martin-diaz/dni-dorso.pdf",
                "https://storage.ipet132.edu.ar/records/martin-diaz/dni-dorso.pdf",
                "application/pdf", 255_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2025, 3, 7), null, "RENAPER",
                adminUserId));

        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_BIRTH_CERTIFICATE,
                "Acta de Nacimiento — Martín Díaz",
                "records/martin-diaz/acta-nacimiento.pdf",
                "https://storage.ipet132.edu.ar/records/martin-diaz/acta-nacimiento.pdf",
                "application/pdf", 530_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2007, 11, 12), null, "Registro Civil de Córdoba",
                adminUserId));

        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_ID_PHOTO,
                "Foto Carnet — Martín Díaz",
                "records/martin-diaz/foto-carnet.jpg",
                "https://storage.ipet132.edu.ar/records/martin-diaz/foto-carnet.jpg",
                "image/jpeg", 88_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2025, 3, 7), null, null,
                adminUserId));

        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_STUDIES_CERTIFICATE,
                "Certificado de Estudios — Martín Díaz",
                "records/martin-diaz/certificado-estudios.pdf",
                "https://storage.ipet132.edu.ar/records/martin-diaz/certificado-estudios.pdf",
                "application/pdf", 395_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2024, 12, 18), null, "Escuela N° 789 Córdoba",
                adminUserId));

        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_HEALTH_CERTIFICATE,
                "Certificado de Aptitud Física — Martín Díaz",
                "records/martin-diaz/certificado-salud.pdf",
                "https://storage.ipet132.edu.ar/records/martin-diaz/certificado-salud.pdf",
                "application/pdf", 305_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2025, 3, 5),
                LocalDate.of(2026, 3, 5),
                "Dr. García — Centro de Salud Sur",
                adminUserId));

        // Opcionales PENDING — entregados pero sin revisar
        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_VACCINATION_CARD,
                "Carnet de Vacunación — Martín Díaz",
                "records/martin-diaz/carnet-vacunacion.pdf",
                "https://storage.ipet132.edu.ar/records/martin-diaz/carnet-vacunacion.pdf",
                "application/pdf", 425_000L,
                DocumentStatus.PENDING,
                LocalDate.of(2025, 3, 7), null, "Ministerio de Salud",
                adminUserId));

        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_OUTING_AUTHORIZATION,
                "Autorización de Salidas — Martín Díaz",
                "records/martin-diaz/autorizacion-salidas.pdf",
                "https://storage.ipet132.edu.ar/records/martin-diaz/autorizacion-salidas.pdf",
                "application/pdf", 170_000L,
                DocumentStatus.PENDING,
                LocalDate.of(2025, 3, 7),
                LocalDate.of(2026, 3, 7),
                null,
                adminUserId));

        documentRepository.saveAll(docs);
        log.info("  ✓ Martín Díaz: {} documentos (legajo PARCIAL — opcionales PENDING)",
                docs.size());
    }

    // =========================================================================
    // Ana Gómez — documento REJECTED (testea flujo de revisión y re-entrega)
    // =========================================================================

    private void seedDocumentsForAna(UUID adminUserId) {
        UUID recordId = resolveRecordId(StudentAndParentDataSeeder.STUDENT_4_ID);
        log.info("Seeding documents for Ana Gómez (record: {})...", recordId);

        List<RecordDocumentEntity> docs = new ArrayList<>();

        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_DNI_FRONT,
                "DNI Frente — Ana Gómez",
                "records/ana-gomez/dni-frente.pdf",
                "https://storage.ipet132.edu.ar/records/ana-gomez/dni-frente.pdf",
                "application/pdf", 249_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2025, 3, 10), null, "RENAPER",
                adminUserId));

        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_DNI_BACK,
                "DNI Dorso — Ana Gómez",
                "records/ana-gomez/dni-dorso.pdf",
                "https://storage.ipet132.edu.ar/records/ana-gomez/dni-dorso.pdf",
                "application/pdf", 241_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2025, 3, 10), null, "RENAPER",
                adminUserId));

        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_BIRTH_CERTIFICATE,
                "Acta de Nacimiento — Ana Gómez",
                "records/ana-gomez/acta-nacimiento.pdf",
                "https://storage.ipet132.edu.ar/records/ana-gomez/acta-nacimiento.pdf",
                "application/pdf", 505_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2007, 4, 28), null, "Registro Civil de Córdoba",
                adminUserId));

        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_ID_PHOTO,
                "Foto Carnet — Ana Gómez",
                "records/ana-gomez/foto-carnet.jpg",
                "https://storage.ipet132.edu.ar/records/ana-gomez/foto-carnet.jpg",
                "image/jpeg", 91_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2025, 3, 10), null, null,
                adminUserId));

        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_STUDIES_CERTIFICATE,
                "Certificado de Estudios — Ana Gómez",
                "records/ana-gomez/certificado-estudios.pdf",
                "https://storage.ipet132.edu.ar/records/ana-gomez/certificado-estudios.pdf",
                "application/pdf", 370_000L,
                DocumentStatus.APPROVED,
                LocalDate.of(2024, 12, 20), null, "Escuela N° 321 Córdoba",
                adminUserId));

        // Certificado de salud REJECTED — ilegible, debe re-entregarse
        docs.add(build(recordId,
                DocumentTypeSeeder.DOC_TYPE_HEALTH_CERTIFICATE,
                "Certificado de Aptitud Física — Ana Gómez",
                "records/ana-gomez/certificado-salud.pdf",
                "https://storage.ipet132.edu.ar/records/ana-gomez/certificado-salud.pdf",
                "application/pdf", 102_000L,
                DocumentStatus.REJECTED,
                LocalDate.of(2025, 3, 10),
                LocalDate.of(2026, 3, 10),
                "Centro de Salud Norte",
                adminUserId));

        documentRepository.saveAll(docs);
        log.info("  ✓ Ana Gómez: {} documentos (certificado salud REJECTED — re-entrega pendiente)",
                docs.size());
    }

    // =========================================================================
    // Builder helper
    // =========================================================================

    private RecordDocumentEntity build(
            UUID recordId,
            UUID documentTypeId,
            String title,
            String filePath,
            String fileName,
            String mimeType,
            long fileSizeBytes,
            DocumentStatus status,
            LocalDate issueDate,
            LocalDate expiryDate,
            String issuingAuthority,
            UUID uploadedBy) {

        RecordDocumentEntity e = new RecordDocumentEntity();
        e.setDocumentId(UUID.randomUUID());
        e.setRecordId(recordId);
        e.setDocumentTypeId(documentTypeId);
        e.setTitle(title);
        e.setFilePath(filePath);
        e.setFileName(fileName);
        e.setMimeType(mimeType);
        e.setFileSize(fileSizeBytes);
        e.setStatus(status);
        e.setIssueDate(issueDate);
        e.setExpiryDate(expiryDate);
        e.setIssuingAuthority(issuingAuthority);
        e.setUploadedAt(LocalDateTime.now());
        e.setUploadedBy(uploadedBy);
        e.setUpdatedAt(LocalDateTime.now());
        return e;
    }

    // =========================================================================
    // Statistics
    // =========================================================================

    private void logStatistics() {
        long total    = documentRepository.count();
        log.info("Record Document Statistics:");
        log.info("  - Total documents : {}", total);
        log.info("  - Lucas Romero    : 8 docs (legajo COMPLETO)");
        log.info("  - Sofía Torres    : 5 docs (legajo INCOMPLETO)");
        log.info("  - Martín Díaz     : 8 docs (legajo PARCIAL)");
        log.info("  - Ana Gómez       : 6 docs (certificado salud REJECTED)");
    }
}
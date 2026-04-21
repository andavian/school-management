package org.school.management.students.records.infrastructure.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.students.records.infrastructure.persistence.entity.DocumentTypeEntity;
import org.school.management.students.records.infrastructure.persistence.repository.DocumentTypeJpaRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Seeder para el catálogo de tipos de documento del legajo estudiantil.
 *
 * Siembra los 12 tipos de documento estándar del IPET 132 organizados
 * en cuatro categorías: PERSONAL, ACADEMIC, MEDICAL, LEGAL.
 *
 * Los UUIDs son fijos para que otros seeders puedan referenciarlos
 * (por ejemplo, un seeder de RecordDocuments en el futuro).
 *
 * @Order(3) — ejecuta después de geography (2) y antes de academic (5)
 */
@Component
@Profile("dev")
@Order(3)
@RequiredArgsConstructor
@Slf4j
public class DocumentTypeSeeder implements ApplicationRunner {

    private final DocumentTypeJpaRepository documentTypeJpaRepository;

    // =========================================================================
    // UUIDs fijos — referenciales para otros seeders
    // =========================================================================

    // PERSONAL
    public static final UUID DOC_TYPE_DNI_FRONT          = UUID.fromString("a1000001-0000-0000-0000-000000000001");
    public static final UUID DOC_TYPE_DNI_BACK           = UUID.fromString("a1000001-0000-0000-0000-000000000002");
    public static final UUID DOC_TYPE_BIRTH_CERTIFICATE  = UUID.fromString("a1000001-0000-0000-0000-000000000003");
    public static final UUID DOC_TYPE_ADDRESS_CERTIFICATE= UUID.fromString("a1000001-0000-0000-0000-000000000004");
    public static final UUID DOC_TYPE_ID_PHOTO           = UUID.fromString("a1000001-0000-0000-0000-000000000005");
    public static final UUID DOC_TYPE_IMAGE_AUTHORIZATION= UUID.fromString("a1000001-0000-0000-0000-000000000006");

    // ACADEMIC
    public static final UUID DOC_TYPE_STUDIES_CERTIFICATE= UUID.fromString("a1000001-0000-0000-0000-000000000007");
    public static final UUID DOC_TYPE_SCHOOL_PASS        = UUID.fromString("a1000001-0000-0000-0000-000000000008");
    public static final UUID DOC_TYPE_PREV_REPORT_CARD   = UUID.fromString("a1000001-0000-0000-0000-000000000009");
    public static final UUID DOC_TYPE_OUTING_AUTHORIZATION=UUID.fromString("a1000001-0000-0000-0000-000000000010");

    // MEDICAL
    public static final UUID DOC_TYPE_HEALTH_CERTIFICATE = UUID.fromString("a1000001-0000-0000-0000-000000000011");
    public static final UUID DOC_TYPE_VACCINATION_CARD   = UUID.fromString("a1000001-0000-0000-0000-000000000012");

    // =========================================================================
    // Runner
    // =========================================================================

    @Override
    public void run(ApplicationArguments args) {
        log.info("=".repeat(80));
        log.info("Starting Document Type Seeder...");
        log.info("=".repeat(80));

        if (documentTypeJpaRepository.count() > 0) {
            log.info("Document type data already exists. Skipping seeder.");
            return;
        }

        try {
            seedDocumentTypes();
            log.info("=".repeat(80));
            log.info("Document Type Seeder completed successfully!");
            logStatistics();
            log.info("=".repeat(80));
        } catch (Exception e) {
            log.error("Error seeding document type data", e);
            throw e;
        }
    }

    // =========================================================================
    // Seed
    // =========================================================================

    private void seedDocumentTypes() {
        List<DocumentTypeEntity> types = List.of(

                // ── PERSONAL ─────────────────────────────────────────────────
                build(DOC_TYPE_DNI_FRONT,
                        "DNI Frente",
                        "DNI_FRONT",
                        "PERSONAL",
                        true,
                        "Frente del Documento Nacional de Identidad vigente",
                        null),

                build(DOC_TYPE_DNI_BACK,
                        "DNI Dorso",
                        "DNI_BACK",
                        "PERSONAL",
                        true,
                        "Dorso del Documento Nacional de Identidad vigente",
                        null),

                build(DOC_TYPE_BIRTH_CERTIFICATE,
                        "Acta de Nacimiento",
                        "BIRTH_CERTIFICATE",
                        "PERSONAL",
                        true,
                        "Partida o acta de nacimiento emitida por el Registro Civil",
                        null),

                build(DOC_TYPE_ADDRESS_CERTIFICATE,
                        "Certificado de Domicilio",
                        "ADDRESS_CERTIFICATE",
                        "PERSONAL",
                        false,
                        "Certificado de domicilio emitido por autoridad policial o municipal",
                        2),

                build(DOC_TYPE_ID_PHOTO,
                        "Foto Carnet",
                        "ID_PHOTO",
                        "PERSONAL",
                        true,
                        "Fotografía tamaño carnet 4x4 fondo blanco",
                        null),

                build(DOC_TYPE_IMAGE_AUTHORIZATION,
                        "Autorización de Imagen",
                        "IMAGE_AUTHORIZATION",
                        "LEGAL",
                        false,
                        "Autorización firmada por tutor para uso de imagen del menor en comunicaciones institucionales",
                        null),

                // ── ACADEMIC ─────────────────────────────────────────────────
                build(DOC_TYPE_STUDIES_CERTIFICATE,
                        "Certificado de Estudios",
                        "STUDIES_CERTIFICATE",
                        "ACADEMIC",
                        true,
                        "Certificado de estudios del nivel anterior o del establecimiento de procedencia",
                        null),

                build(DOC_TYPE_SCHOOL_PASS,
                        "Pase Escolar",
                        "SCHOOL_PASS",
                        "ACADEMIC",
                        false,
                        "Pase escolar emitido por el establecimiento de origen en caso de traslado",
                        null),

                build(DOC_TYPE_PREV_REPORT_CARD,
                        "Boletín Anterior",
                        "PREVIOUS_REPORT_CARD",
                        "ACADEMIC",
                        false,
                        "Boletín de calificaciones del año o establecimiento anterior",
                        1),

                build(DOC_TYPE_OUTING_AUTHORIZATION,
                        "Autorización de Salidas",
                        "OUTING_AUTHORIZATION",
                        "LEGAL",
                        false,
                        "Autorización del tutor para que el alumno pueda retirarse solo del establecimiento",
                        1),

                // ── MEDICAL ──────────────────────────────────────────────────
                build(DOC_TYPE_HEALTH_CERTIFICATE,
                        "Certificado de Salud",
                        "HEALTH_CERTIFICATE",
                        "MEDICAL",
                        true,
                        "Certificado de aptitud física emitido por médico habilitado",
                        1),

                build(DOC_TYPE_VACCINATION_CARD,
                        "Carnet de Vacunación",
                        "VACCINATION_CARD",
                        "MEDICAL",
                        false,
                        "Libreta o carnet de vacunación con el esquema completo según edad",
                        null)
        );

        documentTypeJpaRepository.saveAll(types);
        log.info("✓ Created {} document types", types.size());
    }

    // =========================================================================
    // Builder helper
    // =========================================================================

    private DocumentTypeEntity build(
            UUID id,
            String name,
            String code,
            String category,
            boolean mandatory,
            String description,
            Integer validForYears) {

        DocumentTypeEntity e = new DocumentTypeEntity();
        e.setDocumentTypeId(id);
        e.setName(name);
        e.setCode(code);
        e.setCategory(category);
        e.setMandatory(mandatory);
        e.setDescription(description);
        e.setValidForYears(validForYears);
        e.setActive(true);
        return e;
    }

    // =========================================================================
    // Statistics
    // =========================================================================

    private void logStatistics() {
        long total     = documentTypeJpaRepository.count();
        long mandatory = documentTypeJpaRepository.findAll().stream()
                .filter(DocumentTypeEntity::isMandatory).count();
        long personal  = documentTypeJpaRepository.findAllByCategory("PERSONAL").size();
        long academic  = documentTypeJpaRepository.findAllByCategory("ACADEMIC").size();
        long medical   = documentTypeJpaRepository.findAllByCategory("MEDICAL").size();
        long legal     = documentTypeJpaRepository.findAllByCategory("LEGAL").size();

        log.info("Document Type Statistics:");
        log.info("  - Total types   : {}", total);
        log.info("  - Mandatory     : {}", mandatory);
        log.info("  - PERSONAL      : {}", personal);
        log.info("  - ACADEMIC      : {}", academic);
        log.info("  - MEDICAL       : {}", medical);
        log.info("  - LEGAL         : {}", legal);
    }
}
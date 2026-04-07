package org.school.management.resources.infrastructure.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.resources.domain.model.Resource;
import org.school.management.resources.domain.model.ResourceUnit;
import org.school.management.resources.domain.repository.ResourceRepository;
import org.school.management.resources.domain.repository.ResourceUnitRepository;
import org.school.management.resources.domain.valueobject.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
@Order(8) // Después de attendance (6) y materials (7)
public class ResourcesDataSeeder implements CommandLineRunner {

    private final ResourceRepository resourceRepository;
    private final ResourceUnitRepository resourceUnitRepository;

    @Override
    public void run(String... args) {
        if (!resourceRepository.findAllActive().isEmpty()) {
            log.debug("Seed de resources omitido: ya existen datos.");
            return;
        }

        log.info("Ejecutando seed de recursos didácticos (perfil dev)...");
        UUID adminId = UUID.fromString("00000000-0000-0000-0000-000000000001"); // ADMIN dev por defecto

        // ─── 1. PROYECTORES ──────────────────────────────────────────────
        Resource projector = Resource.create(ResourceId.generate(), "Proyector Epson PowerLite", "PROY-EPSON-01",
                ResourceType.PROJECTOR, "Proyector 4000 lúmenes para aulas principales", "Depósito A1", true, "Incluye control remoto y bolsa", adminId);
        resourceRepository.save(projector);

        for (int i = 1; i <= 4; i++) {
            resourceUnitRepository.save(ResourceUnit.create(UnitId.generate(), projector.getResourceId(),
                    String.format("PROY-00%d", i), String.format("EPX%05dSN", i * 100), ConditionStatus.GOOD));
        }

        // ─── 2. NETBOOKS LENOVO ──────────────────────────────────────────
        Resource netbook = Resource.create(ResourceId.generate(), "Netbook Lenovo V15", "NETBOOK-LENOVO",
                ResourceType.LAPTOP, "Netbooks para estudiantes - 4GB RAM, 128GB SSD", "Laboratorio 2", true, "Cargador incluido", adminId);
        resourceRepository.save(netbook);

        for (int i = 1; i <= 10; i++) {
            resourceUnitRepository.save(ResourceUnit.create(UnitId.generate(), netbook.getResourceId(),
                    String.format("NB-LENOVO-%02d", i), String.format("PF2X%04d", i), ConditionStatus.GOOD));
        }

        // ─── 3. SALA MULTIMEDIA ──────────────────────────────────────────
        Resource multimedia = Resource.create(ResourceId.generate(), "Sala Multimedia Piso 1", "SALA-MULTIMEDIA-01",
                ResourceType.MULTIMEDIA_ROOM, "Sala equipada con 20 PCs, proyector y sonido", "Piso 1 - Ala Este", true, "Requiere reserva de sala completa", adminId);
        resourceRepository.save(multimedia);

        // La sala es 1 unidad física indivisible
        resourceUnitRepository.save(ResourceUnit.create(UnitId.generate(), multimedia.getResourceId(),
                "SALA-MULTI-01", "NO-APLICA", ConditionStatus.GOOD));

        log.info("Seed de resources completado: {} recursos, {} unidades físicas.", 3, 15);
    }
}
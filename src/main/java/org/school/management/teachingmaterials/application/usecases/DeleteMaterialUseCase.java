package org.school.management.teachingmaterials.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.storage.domain.service.StorageService;
import org.school.management.teachers.domain.valueobject.TeacherId;
import org.school.management.teachingmaterials.domain.exception.TeachingMaterialAccessDeniedException;
import org.school.management.teachingmaterials.domain.exception.TeachingMaterialNotFoundException;
import org.school.management.teachingmaterials.domain.model.TeachingMaterial;
import org.school.management.teachingmaterials.domain.repository.TeachingMaterialRepository;
import org.school.management.teachingmaterials.domain.valueobject.TeachingMaterialId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Caso de uso: eliminar un material pedagógico de OCI y de BD.
 *
 * <p>Orden deliberado: primero se elimina el objeto de OCI, luego el registro en BD.
 * Si falla OCI, la transacción no llega a confirmar — no quedan registros huérfanos.
 * Si falla BD después de OCI, queda un objeto huérfano en OCI (aceptable — no afecta
 * integridad de datos, y puede limpiarse manualmente).</p>
 *
 * @param teacherId  null si el invocador es ADMIN/STAFF (bypass de validación de propiedad)
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DeleteMaterialUseCase {

    private final TeachingMaterialRepository materialRepository;
    private final StorageService storageService;

    public void execute(UUID materialId, UUID teacherId) {
        log.info("Deleting teaching material: {}, requestedBy: {}", materialId, teacherId);

        TeachingMaterial material = materialRepository
                .findById(TeachingMaterialId.of(materialId))
                .orElseThrow(() -> TeachingMaterialNotFoundException.byId(materialId));

        // Validar propiedad — solo si viene teacherId (null = ADMIN/STAFF bypass)
        if (teacherId != null && !material.belongsToTeacher(TeacherId.from(teacherId))) {
            throw TeachingMaterialAccessDeniedException.notOwner(materialId, teacherId);
        }

        // Eliminar de OCI primero
        try {
            storageService.delete(material.getFilePath());
            log.info("File deleted from OCI — object: {}", material.getFilePath());
        } catch (Exception e) {
            log.error("Failed to delete file from OCI — object: {} — {}",
                    material.getFilePath(), e.getMessage());
            // Relanzar — no queremos eliminar el registro en BD si OCI falló
            throw new IllegalStateException(
                    "Could not delete file from storage: " + e.getMessage(), e);
        }

        // Eliminar registro en BD
        materialRepository.delete(TeachingMaterialId.of(materialId));

        log.info("TeachingMaterial deleted — materialId: {}", materialId);
    }
}
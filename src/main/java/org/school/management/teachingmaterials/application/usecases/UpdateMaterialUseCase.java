package org.school.management.teachingmaterials.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.teachers.domain.valueobject.TeacherId;
import org.school.management.teachingmaterials.application.dto.request.UpdateMaterialRequest;
import org.school.management.teachingmaterials.application.dto.response.TeachingMaterialResponse;
import org.school.management.teachingmaterials.application.mapper.TeachingMaterialApplicationMapper;
import org.school.management.teachingmaterials.domain.exception.TeachingMaterialAccessDeniedException;
import org.school.management.teachingmaterials.domain.exception.TeachingMaterialNotFoundException;
import org.school.management.teachingmaterials.domain.model.TeachingMaterial;
import org.school.management.teachingmaterials.domain.repository.TeachingMaterialRepository;
import org.school.management.teachingmaterials.domain.valueobject.TeachingMaterialId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Caso de uso: actualizar metadata de un material pedagógico.
 *
 * <p>Solo el profesor dueño puede actualizar su propio material.
 * ADMIN puede hacerlo también — el controller maneja la lógica de rol
 * pasando {@code null} como teacherId para bypass de la validación de propiedad.</p>
 *
 * <p>No permite cambiar el archivo — para eso hay que eliminar y subir de nuevo.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UpdateMaterialUseCase {

    private final TeachingMaterialRepository materialRepository;
    private final TeachingMaterialApplicationMapper mapper;

    /**
     * @param materialId  ID del material a actualizar
     * @param request     campos a actualizar (PATCH semántico — null = conservar)
     * @param teacherId   ID del profesor que hace la acción, o {@code null} si es ADMIN/STAFF
     */
    public TeachingMaterialResponse execute(UUID materialId,
                                            UpdateMaterialRequest request,
                                            UUID teacherId) {
        log.info("Updating teaching material: {}", materialId);

        TeachingMaterial material = materialRepository
                .findById(TeachingMaterialId.of(materialId))
                .orElseThrow(() -> TeachingMaterialNotFoundException.byId(materialId));

        // Validar propiedad — solo si viene teacherId (null = ADMIN bypass)
        if (teacherId != null && !material.belongsToTeacher(TeacherId.from(teacherId))) {
            throw TeachingMaterialAccessDeniedException.notOwner(materialId, teacherId);
        }

        // PATCH semántico — conservar valores existentes si no vienen en el request
        String newTitle = request.title() != null
                ? request.title() : material.getTitle();
        String newDescription = request.description() != null
                ? request.description() : material.getDescription();
        var newType = request.materialType() != null
                ? request.materialType() : material.getMaterialType();
        boolean newVisible = request.visibleToStudents() != null
                ? request.visibleToStudents() : material.isVisibleToStudents();

        material.updateMetadata(newTitle, newDescription, newType, newVisible);

        TeachingMaterial saved = materialRepository.save(material);

        log.info("TeachingMaterial updated — materialId: {}", materialId);

        return mapper.toResponse(saved);
    }
}
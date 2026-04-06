package org.school.management.teachingmaterials.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

import java.util.UUID;

public class TeachingMaterialAccessDeniedException extends DomainException {

    public TeachingMaterialAccessDeniedException(String message) {
        super(message);
    }

    public static TeachingMaterialAccessDeniedException notOwner(UUID materialId, UUID teacherId) {
        return new TeachingMaterialAccessDeniedException(
                "Teacher " + teacherId + " is not the owner of material " + materialId);
    }
}
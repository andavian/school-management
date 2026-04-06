package org.school.management.teachingmaterials.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

import java.util.UUID;

public class TeachingMaterialNotFoundException extends DomainException {

    public TeachingMaterialNotFoundException(String message) {
        super(message);
    }

    public static TeachingMaterialNotFoundException byId(UUID materialId) {
        return new TeachingMaterialNotFoundException(
                "Teaching material not found with id: " + materialId);
    }
}
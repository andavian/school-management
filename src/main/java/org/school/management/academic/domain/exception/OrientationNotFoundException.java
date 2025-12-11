package org.school.management.academic.domain.exception;

import org.school.management.academic.domain.valueobject.ids.OrientationId;

public class OrientationNotFoundException extends RuntimeException {
    public OrientationNotFoundException(OrientationId id) {
        super("Orientation not found: " + id);
    }

    public OrientationNotFoundException(String code) {
        super("Orientation not found with code: " + code);
    }
}

package org.school.management.academic.domain.valueobject.enums;

public enum CourseStatus {
    ACTIVE("Activo"),
    INACTIVE("Inactivo"),
    COMPLETED("Completado");

    private final String displayName;

    CourseStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

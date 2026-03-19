package org.school.management.course.domain.valueobject;

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

    public boolean isTerminal() {
        return this == COMPLETED || this == INACTIVE;
    }
}

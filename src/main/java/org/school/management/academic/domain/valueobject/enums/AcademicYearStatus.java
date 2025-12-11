package org.school.management.academic.domain.valueobject.enums;

public enum AcademicYearStatus {
    PENDING("Pendiente"),
    ACTIVE("Activo"),
    CLOSED("Cerrado");

    private final String displayName;

    AcademicYearStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
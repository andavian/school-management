package org.school.management.academic.domain.valueobject.enums;

public enum RegistryStatus {
    ACTIVE("Activo"),
    FULL("Lleno"),
    CLOSED("Cerrado");

    private final String displayName;

    RegistryStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

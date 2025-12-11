package org.school.management.academic.domain.valueobject.enums;

public enum Shift {
    MORNING("Mañana"),
    AFTERNOON("Tarde"),
    EVENING("Vespertino");

    private final String displayName;

    Shift(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
package org.school.management.students.domain.valueobject;



public enum Relationship {
    FATHER("Padre"),
    MOTHER("Madre"),
    GUARDIAN("Tutor Legal"),
    GRANDPARENT("Abuelo/a"),
    SIBLING("Hermano/a"),
    OTHER("Otro");

    private final String displayName;

    Relationship(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
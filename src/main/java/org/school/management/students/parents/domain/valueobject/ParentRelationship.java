package org.school.management.students.parents.domain.valueobject;

public enum ParentRelationship {
    FATHER("Padre"),
    MOTHER("Madre"),
    GUARDIAN("Tutor legal"),
    GRANDPARENT("Abuelo/a"),
    SIBLING("Hermano/a mayor"),
    OTHER("Otro");

    private final String displayName;

    ParentRelationship(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
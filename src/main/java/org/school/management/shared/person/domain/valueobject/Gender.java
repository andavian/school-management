// src/main/java/org/school/management/shared/person/domain/valueobject/Gender.java
package org.school.management.shared.person.domain.valueobject;

public enum Gender {
    MASCULINE("Masculino"),
    FEMININE("Femenino"),
    OTHER("Otro"),
    NO_BINARY("No Binario");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
// src/main/java/org/school/management/shared/person/domain/valueobject/Gender.java
package org.school.management.shared.person.domain.valueobject;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("Masculino"),
    FEMALE("Femenino"),
    OTHER("Otro");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

}
// src/main/java/org/school/management/shared/person/domain/valueobject/BirthPlaceId.java
package org.school.management.shared.person.domain.valueobject;

import java.util.UUID;

public record ResidencePlaceId(UUID value) {
    public static ResidencePlaceId generate() { return new ResidencePlaceId(UUID.randomUUID()); }
    public static ResidencePlaceId of(String value) { return new ResidencePlaceId(UUID.fromString(value)); }
}
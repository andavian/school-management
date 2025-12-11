// src/main/java/org/school/management/shared/person/domain/valueobject/BirthPlaceId.java
package org.school.management.shared.person.domain.valueobject;

import java.util.UUID;

public record BirthPlaceId(UUID value) {
    public static BirthPlaceId generate() { return new BirthPlaceId(UUID.randomUUID()); }
    public static BirthPlaceId of(String value) { return new BirthPlaceId(UUID.fromString(value)); }
}
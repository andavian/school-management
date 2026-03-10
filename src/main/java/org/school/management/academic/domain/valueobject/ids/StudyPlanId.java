package org.school.management.academic.domain.valueobject.ids;

import java.util.UUID;

public record StudyPlanId(UUID value) {

    public StudyPlanId {
        if (value == null) throw new IllegalArgumentException("StudyPlanId cannot be null");
    }

    public static StudyPlanId of(UUID value) {
        return new StudyPlanId(value);
    }

    public static StudyPlanId generate() {
        return new StudyPlanId(UUID.randomUUID());
    }

    public static StudyPlanId from(UUID uuid) {
        return new StudyPlanId(uuid);
    }

    public static StudyPlanId from(String id) {
        try {
            return new StudyPlanId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid StudyPlanId format: " + id);
        }
    }

    public String asString() {
        return value.toString();
    }
}

package org.school.management.academic.domain.valueobject.ids;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class StudyPlanId {
    UUID value;

    public StudyPlanId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        this.value = value;
    }

    public static StudyPlanId generate() {
        return new StudyPlanId(UUID.randomUUID());
    }

    public static StudyPlanId from(String id) {
        try {
            return new StudyPlanId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UserId format: " + id);
        }
    }

    public static StudyPlanId from(UUID uuid) {
        return new StudyPlanId(uuid);
    }

    public String asString() {
        return value.toString();
    }
}


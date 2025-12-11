
package org.school.management.students.enrollment.domain.valueobject;

import java.util.UUID;

public record EnrollmentId(UUID value) {

    public static EnrollmentId generate() {
        return new EnrollmentId(UUID.randomUUID());
    }

    public static EnrollmentId of(String value) {
        return new EnrollmentId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

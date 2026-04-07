package org.school.management.resources.domain.exception;

import org.school.management.shared.domain.exception.DomainException;
import org.school.management.resources.domain.valueobject.ResourceId;

import java.time.LocalDate;
import java.time.LocalTime;

public class InsufficientResourceUnitsException extends DomainException {
    public InsufficientResourceUnitsException(String message) {
        super(message);
    }

    public static InsufficientResourceUnitsException withDetails(ResourceId resourceId, int requested, int available, LocalDate date, LocalTime start, LocalTime end) {
        return new InsufficientResourceUnitsException(
                String.format("Insufficient available units for resource %s on %s %s-%s. Requested: %d, Available: %d",
                        resourceId.value(), date, start, end, requested, available)
        );
    }
}
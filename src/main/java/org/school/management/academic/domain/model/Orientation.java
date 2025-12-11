package org.school.management.academic.domain.model;

import lombok.Builder;
import lombok.Value;
import org.school.management.academic.domain.valueobject.OrientationCode;
import org.school.management.academic.domain.valueobject.enums.AcademicYearStatus;
import org.school.management.academic.domain.valueobject.ids.OrientationId;
import org.school.management.academic.domain.valueobject.YearLevel;

import java.time.LocalDateTime;
import java.util.Objects;

@Value
@Builder(toBuilder = true)  // ← Esto genera el copy constructor correctamente
public class Orientation {

    OrientationId orientationId;
    String name;
    OrientationCode code;
    String description;
    YearLevel availableFromYear;
    Boolean isActive;                    // ← Sin @With
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public static Orientation create(
            String name,
            String code,
            String description,
            int availableFromYear
    ) {
        Objects.requireNonNull(name, "Name is required");
        Objects.requireNonNull(code, "Code is required");

        return Orientation.builder()
                .orientationId(OrientationId.generate())
                .name(name.trim())
                .code(OrientationCode.of(code))
                .description(description != null ? description.trim() : null)
                .availableFromYear(YearLevel.of(availableFromYear))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ← Usamos toBuilder() en lugar de @With
    public Orientation deactivate() {
        return this.toBuilder()
                .isActive(false)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Orientation activate() {
        return this.toBuilder()
                .isActive(true)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public String getCodeAsString() {
        return code.getValue();
    }

    public int getAvailableFromYearValue() {
        return availableFromYear.getValue();
    }
}
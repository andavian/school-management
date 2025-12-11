package org.school.management.academic.domain.model;

import lombok.Builder;
import lombok.Value;
import org.school.management.academic.domain.valueobject.*;
import org.school.management.academic.domain.valueobject.enums.AcademicYearStatus;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Value
@Builder(toBuilder = true)
public class AcademicYear {

    AcademicYearId academicYearId;
    Year year;
    LocalDate startDate;
    LocalDate endDate;
    AcademicYearStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public static AcademicYear create(int yearValue, LocalDate startDate, LocalDate endDate, boolean isCurrent) {
        Objects.requireNonNull(startDate, "Start date cannot be null");
        Objects.requireNonNull(endDate, "End date cannot be null");


        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        return AcademicYear.builder()
                .academicYearId(AcademicYearId.generate())
                .year(Year.of(yearValue))
                .startDate(startDate)
                .endDate(endDate)
                .status(isCurrent ? AcademicYearStatus.ACTIVE : AcademicYearStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ← Usamos toBuilder() en lugar de withX()
    public AcademicYear activate() {
        return this.toBuilder()
                .status(AcademicYearStatus.ACTIVE)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public AcademicYear close() {
        return this.toBuilder()
                .status(AcademicYearStatus.CLOSED)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public boolean isCurrent() {
        return status == AcademicYearStatus.ACTIVE;
    }

    public int getYearValue() {
        return year.getValue();
    }


}
package org.school.management.academic.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.school.management.academic.domain.valueobject.PeriodNumber;
import org.school.management.academic.domain.valueobject.enums.AcademicYearStatus;
import org.school.management.academic.domain.valueobject.enums.PeriodStatus;
import org.school.management.academic.domain.valueobject.ids.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
public class EvaluationPeriod {
    PeriodId periodId;
    AcademicYearId academicYearId;
    PeriodNumber periodNumber;
    String name;
    LocalDate startDate;
    LocalDate endDate;
    Boolean isCurrent;
    PeriodStatus status;
    LocalDateTime createdAt;
    LocalDateTime closedAt;


    public static EvaluationPeriod create(
            AcademicYearId academicYearId,
            int periodNumberValue,
            String name,
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        return EvaluationPeriod.builder()
                .periodId(PeriodId.generate())
                .academicYearId(academicYearId)
                .periodNumber(PeriodNumber.of(periodNumberValue))
                .name(name)
                .startDate(startDate)
                .endDate(endDate)
                .isCurrent(false)
                .status(PeriodStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public EvaluationPeriod activate() {
        return this.toBuilder()
                .isCurrent(true)
                .status(PeriodStatus.ACTIVE)
                .build();
    }

    public EvaluationPeriod close() {
        return this.toBuilder()
                .isCurrent(false)
                .status(PeriodStatus.CLOSED)
                .closedAt(LocalDateTime.now())
                .build();
    }

    public boolean isActive() {
        return status == PeriodStatus.ACTIVE;
    }

    public boolean isInProgress(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }




}

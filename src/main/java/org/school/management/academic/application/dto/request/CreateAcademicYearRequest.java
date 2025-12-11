package org.school.management.academic.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;


import java.time.LocalDate;

public record CreateAcademicYearRequest (
        @NotNull(message = "Year is required")
        @Min(value = 2020, message = "Year must be >= 2020")
        @Max(value = 2100, message = "Year must be <= 2100")
        Integer year,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate,

        @NotNull(message = "Current status must be specified")
        Boolean isCurrent

){


}


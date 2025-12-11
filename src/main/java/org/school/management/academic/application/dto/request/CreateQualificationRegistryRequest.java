package org.school.management.academic.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public record CreateQualificationRegistryRequest (
        @NotBlank(message = "Academic year ID is required")
        String academicYearId,

                @NotNull(message = "Start folio is required")
@Min(value = 1, message = "Start folio must be >= 1")
Integer startFolio,

@NotNull(message = "End folio is required")
@Min(value = 1, message = "End folio must be >= 1")
Integer endFolio,

@NotNull(message = "Max folios is required")
@Min(value = 1, message = "Max folios must be >= 1")
Integer maxFolios
){


}

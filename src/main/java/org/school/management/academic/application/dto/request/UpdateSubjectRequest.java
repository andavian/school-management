package org.school.management.academic.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.school.management.academic.domain.valueobject.WeeklyHours;

public record UpdateSubjectRequest (
        @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
        String name,

                @Min(value = 1, message = "Weekly hours must be >= 1")
@Max(value = 20, message = "Weekly hours must be <= 20")
        WeeklyHours weeklyHours,

@Size(max = 500, message = "Description must not exceed 500 characters")
String description
){


}

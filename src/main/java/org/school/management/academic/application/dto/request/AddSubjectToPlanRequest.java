package org.school.management.academic.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AddSubjectToPlanRequest (
        @NotBlank(message = "Subject ID is required")
        String subjectId
){


}

package org.school.management.academic.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AssignHomeroomTeacherRequest (
        @NotBlank(message = "Teacher ID is required")
        String teacherId
){


}

package org.school.management.course.application.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record EnrollStudentRequest(
        @NotNull(message = "enrollmentId is required")
        UUID enrollmentId,

        @NotNull(message = "courseSubjectId is required")
        UUID courseSubjectId
) {}
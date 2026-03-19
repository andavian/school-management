package org.school.management.course.application.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AssignTeacherRequest(
        @NotNull(message = "teacherId is required")
        UUID teacherId
) {}
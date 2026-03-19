package org.school.management.course.application.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateCourseSubjectRequest(

        @NotNull(message = "gradeLevelId is required")
        UUID gradeLevelId,

        @NotNull(message = "subjectId is required")
        UUID subjectId,

        @NotNull(message = "academicYearId is required")
        UUID academicYearId,

        UUID teacherId,       // opcional — se puede asignar luego

        String scheduleJson,
        String classroom
) {}
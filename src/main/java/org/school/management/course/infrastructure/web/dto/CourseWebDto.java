package org.school.management.course.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public final class CourseWebDto {

    private CourseWebDto() {}

    public record CreateCourseSubjectWebRequest(
            @NotNull UUID gradeLevelId,
            @NotNull UUID subjectId,
            @NotNull UUID academicYearId,
            UUID teacherId,
            String scheduleJson,
            String classroom
    ) {}

    public record AssignTeacherWebRequest(
            @NotNull UUID teacherId
    ) {}

    public record EnrollStudentWebRequest(
            @NotNull UUID enrollmentId,
            @NotNull UUID courseSubjectId
    ) {}
}
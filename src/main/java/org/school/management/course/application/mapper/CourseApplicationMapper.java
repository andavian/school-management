package org.school.management.course.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.school.management.course.application.dto.response.CourseSubjectResponse;
import org.school.management.course.application.dto.response.StudentCourseSubjectResponse;
import org.school.management.course.domain.model.CourseSubject;
import org.school.management.course.domain.model.StudentCourseSubject;

@Mapper(componentModel = "spring")
public interface CourseApplicationMapper {

    @Mapping(target = "courseSubjectId", expression = "java(domain.getCourseSubjectId().value())")
    @Mapping(target = "gradeLevelId",    expression = "java(domain.getGradeLevelId().value())")
    @Mapping(target = "subjectId",       expression = "java(domain.getSubjectId().value())")
    @Mapping(target = "teacherId",       expression = "java(domain.getTeacherId() != null ? domain.getTeacherId().value() : null)")
    @Mapping(target = "academicYearId",  expression = "java(domain.getAcademicYearId().value())")
    CourseSubjectResponse toResponse(CourseSubject domain);

    @Mapping(target = "studentCourseSubjectId", expression = "java(domain.getStudentCourseSubjectId().value())")
    StudentCourseSubjectResponse toResponse(StudentCourseSubject domain);
}
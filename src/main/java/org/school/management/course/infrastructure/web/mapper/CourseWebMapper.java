package org.school.management.course.infrastructure.web.mapper;

import org.mapstruct.Mapper;
import org.school.management.course.application.dto.request.AssignTeacherRequest;
import org.school.management.course.application.dto.request.CreateCourseSubjectRequest;
import org.school.management.course.application.dto.request.EnrollStudentRequest;
import org.school.management.course.infrastructure.web.dto.CourseWebDto;

@Mapper(componentModel = "spring")
public interface CourseWebMapper {

    CreateCourseSubjectRequest toRequest(CourseWebDto.CreateCourseSubjectWebRequest web);

    AssignTeacherRequest toRequest(CourseWebDto.AssignTeacherWebRequest web);

    EnrollStudentRequest toRequest(CourseWebDto.EnrollStudentWebRequest web);
}
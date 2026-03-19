package org.school.management.course.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.course.application.dto.response.StudentCourseSubjectResponse;
import org.school.management.course.application.mapper.CourseApplicationMapper;
import org.school.management.course.domain.repository.StudentCourseSubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GetStudentCoursesUseCase {

    private final StudentCourseSubjectRepository studentCourseSubjectRepository;
    private final CourseApplicationMapper mapper;

    public List<StudentCourseSubjectResponse> execute(UUID enrollmentId) {
        return studentCourseSubjectRepository
                .findByEnrollment(enrollmentId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
package org.school.management.course.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.course.application.dto.request.EnrollStudentRequest;
import org.school.management.course.application.dto.response.StudentCourseSubjectResponse;
import org.school.management.course.application.mapper.CourseApplicationMapper;
import org.school.management.course.domain.exception.CourseSubjectNotFoundException;
import org.school.management.course.domain.exception.StudentAlreadyEnrolledException;
import org.school.management.course.domain.model.StudentCourseSubject;
import org.school.management.course.domain.repository.CourseSubjectRepository;
import org.school.management.course.domain.repository.StudentCourseSubjectRepository;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EnrollStudentInCourseUseCase {

    private final CourseSubjectRepository courseSubjectRepository;
    private final StudentCourseSubjectRepository studentCourseSubjectRepository;
    private final CourseApplicationMapper mapper;

    public StudentCourseSubjectResponse execute(EnrollStudentRequest request) {
        // Verificar que el curso existe y está activo
        courseSubjectRepository
                .findById(CourseSubjectId.from(request.courseSubjectId()))
                .filter(c -> c.isActive())
                .orElseThrow(() -> CourseSubjectNotFoundException.byId(request.courseSubjectId()));

        // Verificar que el alumno no esté ya inscripto
        if (studentCourseSubjectRepository.existsByEnrollmentAndCourseSubject(
                request.enrollmentId(), request.courseSubjectId())) {
            throw StudentAlreadyEnrolledException.inCourseSubject(
                    request.enrollmentId(), request.courseSubjectId());
        }

        StudentCourseSubject enrolled = StudentCourseSubject.enroll(
                request.enrollmentId(),
                request.courseSubjectId()
        );

        StudentCourseSubject saved = studentCourseSubjectRepository.save(enrolled);
        log.info("Student enrollment {} enrolled in courseSubject {}",
                request.enrollmentId(), request.courseSubjectId());

        return mapper.toResponse(saved);
    }
}
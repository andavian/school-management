package org.school.management.course.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.course.application.dto.request.AssignTeacherRequest;
import org.school.management.course.application.dto.response.CourseSubjectResponse;
import org.school.management.course.application.mapper.CourseApplicationMapper;
import org.school.management.course.domain.exception.CourseSubjectNotFoundException;
import org.school.management.course.domain.model.CourseSubject;
import org.school.management.course.domain.repository.CourseSubjectRepository;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.teachers.domain.valueobject.TeacherId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AssignTeacherToCourseUseCase {

    private final CourseSubjectRepository courseSubjectRepository;
    private final CourseApplicationMapper mapper;

    public CourseSubjectResponse execute(UUID courseSubjectId, AssignTeacherRequest request) {
        CourseSubject courseSubject = courseSubjectRepository
                .findById(CourseSubjectId.from(courseSubjectId))
                .orElseThrow(() -> CourseSubjectNotFoundException.byId(courseSubjectId));

        courseSubject.assignTeacher(TeacherId.from(request.teacherId()));

        CourseSubject saved = courseSubjectRepository.save(courseSubject);
        log.info("Teacher {} assigned to courseSubject {}",
                request.teacherId(), courseSubjectId);

        return mapper.toResponse(saved);
    }
}
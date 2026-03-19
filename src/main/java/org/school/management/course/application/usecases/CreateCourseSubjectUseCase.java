package org.school.management.course.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.academic.domain.valueobject.ids.SubjectId;
import org.school.management.course.application.dto.request.CreateCourseSubjectRequest;
import org.school.management.course.application.dto.response.CourseSubjectResponse;
import org.school.management.course.application.mapper.CourseApplicationMapper;
import org.school.management.course.domain.exception.CourseSubjectAlreadyExistsException;
import org.school.management.course.domain.model.CourseSubject;
import org.school.management.course.domain.repository.CourseSubjectRepository;
import org.school.management.teachers.domain.valueobject.TeacherId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CreateCourseSubjectUseCase {

    private final CourseSubjectRepository courseSubjectRepository;
    private final CourseApplicationMapper mapper;

    public CourseSubjectResponse execute(CreateCourseSubjectRequest request) {
        GradeLevelId gradeLevelId   = GradeLevelId.from(request.gradeLevelId());
        SubjectId    subjectId      = SubjectId.from(request.subjectId());
        AcademicYearId academicYearId = AcademicYearId.from(request.academicYearId());

        if (courseSubjectRepository.existsByGradeLevelAndSubjectAndYear(
                gradeLevelId, subjectId, academicYearId)) {
            throw CourseSubjectAlreadyExistsException.forGradeLevelAndSubject(
                    request.gradeLevelId(), request.subjectId(), 0);
        }

        TeacherId teacherId = request.teacherId() != null
                ? TeacherId.from(request.teacherId())
                : null;

        CourseSubject courseSubject = CourseSubject.create(
                gradeLevelId,
                subjectId,
                academicYearId,
                teacherId
        );

        if (request.scheduleJson() != null || request.classroom() != null) {
            courseSubject.updateSchedule(request.scheduleJson(), request.classroom());
        }

        CourseSubject saved = courseSubjectRepository.save(courseSubject);
        log.info("CourseSubject created: {} for gradeLevel {} subject {}",
                saved.getCourseSubjectId().asString(),
                request.gradeLevelId(),
                request.subjectId());

        return mapper.toResponse(saved);
    }
}
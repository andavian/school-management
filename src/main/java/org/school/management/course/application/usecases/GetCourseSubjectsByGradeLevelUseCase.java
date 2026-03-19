package org.school.management.course.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.course.application.dto.response.CourseSubjectResponse;
import org.school.management.course.application.mapper.CourseApplicationMapper;
import org.school.management.course.domain.repository.CourseSubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GetCourseSubjectsByGradeLevelUseCase {

    private final CourseSubjectRepository courseSubjectRepository;
    private final CourseApplicationMapper mapper;

    public List<CourseSubjectResponse> execute(UUID gradeLevelId, UUID academicYearId) {
        return courseSubjectRepository
                .findByGradeLevelAndYear(
                        GradeLevelId.from(gradeLevelId),
                        AcademicYearId.from(academicYearId))
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
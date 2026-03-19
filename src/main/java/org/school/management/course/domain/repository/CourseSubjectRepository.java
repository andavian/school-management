package org.school.management.course.domain.repository;

import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.academic.domain.valueobject.ids.SubjectId;
import org.school.management.course.domain.model.CourseSubject;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.teachers.domain.valueobject.TeacherId;

import java.util.List;
import java.util.Optional;

public interface CourseSubjectRepository {

    CourseSubject save(CourseSubject courseSubject);

    Optional<CourseSubject> findById(CourseSubjectId courseSubjectId);

    List<CourseSubject> findByGradeLevelAndYear(GradeLevelId gradeLevelId, AcademicYearId academicYearId);

    List<CourseSubject> findByTeacherAndYear(TeacherId teacherId, AcademicYearId academicYearId);

    boolean existsByGradeLevelAndSubjectAndYear(
            GradeLevelId gradeLevelId,
            SubjectId subjectId,
            AcademicYearId academicYearId
    );
}
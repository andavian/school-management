//package org.school.management.course.domain.repository;
//
//import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
//import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
//import org.school.management.academic.domain.valueobject.ids.SubjectId;
//import org.school.management.course.domain.model.CourseSubject;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//public interface CourseSubjectRepository {
//    CourseSubject save(CourseSubject courseSubject);
//
//    Optional<CourseSubject> findById(UUID courseSubjectId);
//
//    List<CourseSubject> findByGradeLevel(GradeLevelId gradeLevelId);
//
//    List<CourseSubject> findByTeacher(UUID teacherId);
//
//    List<CourseSubject> findActiveByGradeLevelAndYear(GradeLevelId gradeLevelId, AcademicYearId academicYearId);
//
//    List<CourseSubject> findTeacherCourses(UUID teacherId, AcademicYearId academicYearId);
//
//    boolean existsByGradeLevelAndSubjectAndYear(GradeLevelId gradeLevelId, SubjectId subjectId, AcademicYearId academicYearId);
//}

//package org.school.management.course.infra.persistence.adapters;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.school.management.course.domain.repository.CourseSubjectRepository;
//import org.school.management.course.infra.persistence.repository.CourseSubjectJpaRepository;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Repository
//@RequiredArgsConstructor
//@Slf4j
//@Transactional(readOnly = true)
//public class CourseSubjectRepositoryAdapter implements CourseSubjectRepository {
//
//    private final CourseSubjectJpaRepository jpaRepository;
//    private final AcademicPersistenceMapper mapper;
//
//    @Override
//    @Transactional
//    public CourseSubject save(CourseSubject courseSubject) {
//        log.debug("Saving course subject: {}", courseSubject.getCourseSubjectId());
//        var entity = mapper.toCourseSubjectEntity(courseSubject);
//        var saved = jpaRepository.save(entity);
//        return mapper.toCourseSubjectDomain(saved);
//    }
//
//    @Override
//    public Optional<CourseSubject> findById(CourseSubjectId courseSubjectId) {
//        return jpaRepository.findById(courseSubjectId.getValue())
//                .map(mapper::toCourseSubjectDomain);
//    }
//
//    @Override
//    public List<CourseSubject> findByGradeLevel(GradeLevelId gradeLevelId) {
//        return jpaRepository.findByGradeLevelId(gradeLevelId.getValue()).stream()
//                .map(mapper::toCourseSubjectDomain)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<CourseSubject> findByTeacher(UUID teacherId) {
//        return jpaRepository.findByTeacherId(teacherId).stream()
//                .map(mapper::toCourseSubjectDomain)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<CourseSubject> findActiveByGradeLevelAndYear(GradeLevelId gradeLevelId, AcademicYearId academicYearId) {
//        return jpaRepository.findActiveByGradeLevelAndYear(gradeLevelId.getValue(), academicYearId.getValue()).stream()
//                .map(mapper::toCourseSubjectDomain)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<CourseSubject> findTeacherCourses(UUID teacherId, AcademicYearId academicYearId) {
//        return jpaRepository.findTeacherCourses(teacherId, academicYearId.getValue()).stream()
//                .map(mapper::toCourseSubjectDomain)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public boolean existsByGradeLevelAndSubjectAndYear(GradeLevelId gradeLevelId, SubjectId subjectId, AcademicYearId academicYearId) {
//        return jpaRepository.existsByGradeLevelIdAndSubjectIdAndAcademicYearId(
//                gradeLevelId.getValue(),
//                subjectId.getValue(),
//                academicYearId.getValue()
//        );
//    }
//}

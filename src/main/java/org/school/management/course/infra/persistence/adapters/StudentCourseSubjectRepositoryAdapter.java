//package org.school.management.course.infra.persistence.adapters;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.school.management.course.domain.repository.StudentCourseSubjectRepository;
//import org.school.management.course.infra.persistence.repository.StudentCourseSubjectJpaRepository;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Repository
//@RequiredArgsConstructor
//@Slf4j
//@Transactional(readOnly = true)
//public class StudentCourseSubjectRepositoryAdapter implements StudentCourseSubjectRepository {
//
//    private final StudentCourseSubjectJpaRepository jpaRepository;
//    private final AcademicPersistenceMapper mapper;
//
//    @Override
//    @Transactional
//    public StudentCourseSubject save(StudentCourseSubject studentCourseSubject) {
//        log.debug("Saving student course subject: {}", studentCourseSubject.getId());
//        var entity = mapper.toStudentCourseSubjectEntity(studentCourseSubject);
//        var saved = jpaRepository.save(entity);
//        return mapper.toStudentCourseSubjectDomain(saved);
//    }
//
//    @Override
//    public Optional<StudentCourseSubject> findById(StudentCourseSubjectId id) {
//        return jpaRepository.findById(id.getValue())
//                .map(mapper::toStudentCourseSubjectDomain);
//    }
//
//    @Override
//    public List<StudentCourseSubject> findByEnrollment(UUID enrollmentId) {
//        return jpaRepository.findByEnrollmentId(enrollmentId).stream()
//                .map(mapper::toStudentCourseSubjectDomain)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public Optional<StudentCourseSubject> findByEnrollmentAndCourseSubject(UUID enrollmentId, CourseSubjectId courseSubjectId) {
//        return jpaRepository.findByEnrollmentIdAndCourseSubjectId(enrollmentId, courseSubjectId.getValue())
//                .map(mapper::toStudentCourseSubjectDomain);
//    }
//
//    @Override
//    public List<StudentCourseSubject> findByEnrollmentAndStatus(UUID enrollmentId, SubjectEnrollmentStatus status) {
//        return jpaRepository.findByEnrollmentAndStatus(enrollmentId, status.name()).stream()
//                .map(mapper::toStudentCourseSubjectDomain)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<StudentCourseSubject> findWithLowAttendance(UUID enrollmentId, BigDecimal minPercentage) {
//        return jpaRepository.findWithLowAttendance(enrollmentId, minPercentage).stream()
//                .map(mapper::toStudentCourseSubjectDomain)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public long countActiveStudents(CourseSubjectId courseSubjectId) {
//        return jpaRepository.countActiveStudents(courseSubjectId.getValue());
//    }
//}

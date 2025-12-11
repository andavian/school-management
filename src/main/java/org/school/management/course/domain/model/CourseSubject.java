//package org.school.management.course.domain.model;
//
//import lombok.Builder;
//import lombok.Value;
//import lombok.With;
//import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
//import org.school.management.academic.domain.valueobject.enums.CourseStatus;
//import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
//import org.school.management.academic.domain.valueobject.ids.SubjectId;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Value
//@Builder(toBuilder = true)
//public class CourseSubject {
//    UUID courseSubjectId;
//    GradeLevelId gradeLevelId;
//    SubjectId subjectId;
//    UUID teacherId;  // Puede ser null temporalmente
//    AcademicYearId academicYearId;
//
//    String scheduleJson;  // JSON con horarios
//    String classroom;
//
//    BigDecimal minPassingGrade;
//    boolean requiresAttendance;
//    int minAttendancePercentage;
//
//    @With
//    CourseStatus status;
//    LocalDateTime createdAt;
//    LocalDateTime updatedAt;
//
//    public static CourseSubject create(
//            GradeLevelId gradeLevelId,
//            SubjectId subjectId,
//            AcademicYearId academicYearId,
//            UUID teacherId
//    ) {
//        return CourseSubject.builder()
//                .courseSubjectId(UUID.generate())
//                .gradeLevelId(gradeLevelId)
//                .subjectId(subjectId)
//                .teacherId(teacherId)
//                .academicYearId(academicYearId)
//                .minPassingGrade(BigDecimal.valueOf(6.00))
//                .requiresAttendance(true)
//                .minAttendancePercentage(75)
//                .status(CourseStatus.ACTIVE)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//    }
//
//    public CourseSubject assignTeacher(UUID teacherId) {
//        return this.toBuilder()
//                .teacherId(teacherId)
//                .updatedAt(LocalDateTime.now())
//                .build();
//    }
//
//    public boolean hasTeacher() {
//        return teacherId != null;
//    }
//}

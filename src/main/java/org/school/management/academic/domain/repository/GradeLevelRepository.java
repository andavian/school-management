package org.school.management.academic.domain.repository;

import org.school.management.academic.domain.valueobject.Division; // Nuevo import
import org.school.management.academic.domain.valueobject.YearLevel;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.academic.domain.valueobject.ids.OrientationId;
import org.school.management.academic.domain.model.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GradeLevelRepository {
    GradeLevel save(GradeLevel gradeLevel);

    Optional<GradeLevel> findById(GradeLevelId gradeLevelId);

    List<GradeLevel> findByAcademicYear(AcademicYearId academicYearId);

    // CAMBIO A VO
    List<GradeLevel> findByAcademicYearAndYearLevel(AcademicYearId academicYearId, YearLevel yearLevel);

    List<GradeLevel> findByOrientation(OrientationId orientationId);

    List<GradeLevel> findActiveGradeLevels();

    List<GradeLevel> findActiveByAcademicYear(AcademicYearId academicYearId);

    List<GradeLevel> findCurrentYearActiveLevels();

    Optional<GradeLevel> findByAcademicYearAndYearLevelAndDivision(
            AcademicYearId academicYearId,
            YearLevel yearLevel,
            Division division
    );

    List<GradeLevel> findByYearLevelAndOrientation(
            AcademicYearId academicYearId,
            YearLevel yearLevel,
            OrientationId orientationId
    );

    List<GradeLevel> findByTeacher(UUID teacherId);


    boolean existsByAcademicYearAndYearLevelAndDivision(
            AcademicYearId academicYearId,
            YearLevel yearLevel,
            Division division
    );

    long countByAcademicYear(AcademicYearId academicYearId);

    void delete(GradeLevelId id);
}
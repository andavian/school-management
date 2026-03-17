package org.school.management.grades.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.grades.application.dto.response.FinalGradeResponse;
import org.school.management.grades.application.mapper.GradesApplicationMapper;
import org.school.management.grades.domain.exception.InvalidGradeException;
import org.school.management.grades.domain.model.FinalGrade;
import org.school.management.grades.domain.model.PeriodGrade;
import org.school.management.grades.domain.repository.FinalGradeRepository;
import org.school.management.grades.domain.repository.PeriodGradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CalculateFinalGradeUseCase {

    private final PeriodGradeRepository periodGradeRepository;
    private final FinalGradeRepository finalGradeRepository;
    private final GradesApplicationMapper mapper;

    public FinalGradeResponse execute(UUID studentCourseSubjectId, UUID academicYearId) {
        log.debug("Calculating final grade for studentCourseSubject: {} academicYear: {}",
                studentCourseSubjectId, academicYearId);

        StudentCourseSubjectId scsId =
                StudentCourseSubjectId.from(studentCourseSubjectId);
        AcademicYearId yearId =
                AcademicYearId.from(academicYearId);

        List<PeriodGrade> periodGrades =
                periodGradeRepository.findByStudentCourseSubject(scsId);

        if (periodGrades.isEmpty()) {
            throw InvalidGradeException.withReason(
                    "No period grades found for studentCourseSubject: "
                            + studentCourseSubjectId
            );
        }

        List<PeriodGrade> validatedPeriodGrades = periodGrades.stream()
                .filter(PeriodGrade::isValidated)
                .toList();

        if (validatedPeriodGrades.isEmpty()) {
            throw InvalidGradeException.withReason(
                    "No validated period grades found for studentCourseSubject: "
                            + studentCourseSubjectId
            );
        }

        List<BigDecimal> grades = validatedPeriodGrades.stream()
                .map(PeriodGrade::getFinalPeriodGrade)
                .toList();

        // Si ya existe una FinalGrade la reemplazamos — recalculo explícito
        FinalGrade finalGrade = FinalGrade.create(scsId, yearId, grades);
        FinalGrade saved = finalGradeRepository.save(finalGrade);

        log.info("Final grade calculated: {} status: {} for studentCourseSubject: {}",
                saved.getFinalGrade(), saved.getStatus(), studentCourseSubjectId);

        return mapper.toFinalGradeResponse(saved);
    }
}
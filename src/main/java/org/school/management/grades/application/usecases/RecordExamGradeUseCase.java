package org.school.management.grades.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.grades.application.dto.request.RecordExamGradeRequest;
import org.school.management.grades.application.dto.response.FinalGradeResponse;
import org.school.management.grades.application.mapper.GradesApplicationMapper;
import org.school.management.grades.domain.exception.GradeNotFoundException;
import org.school.management.grades.domain.exception.InvalidGradeException;
import org.school.management.grades.domain.model.FinalGrade;
import org.school.management.grades.domain.repository.FinalGradeRepository;
import org.school.management.grades.domain.valueobject.FinalGradeStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RecordExamGradeUseCase {

    private final FinalGradeRepository finalGradeRepository;
    private final GradesApplicationMapper mapper;

    public FinalGradeResponse execute(RecordExamGradeRequest request) {
        log.debug("Recording exam grade for studentCourseSubject: {}",
                request.studentCourseSubjectId());

        StudentCourseSubjectId scsId =
                StudentCourseSubjectId.from(request.studentCourseSubjectId());
        AcademicYearId yearId =
                AcademicYearId.from(request.academicYearId());

        FinalGrade finalGrade = finalGradeRepository
                .findByStudentCourseSubjectAndYear(scsId, yearId)
                .orElseThrow(() -> GradeNotFoundException.finalGradeForSubject(
                        request.studentCourseSubjectId(),
                        request.academicYearId()
                ));

        if (finalGrade.getStatus() != FinalGradeStatus.PENDING_EXAM) {
            throw InvalidGradeException.notInPendingExamStatus(
                    request.studentCourseSubjectId()
            );
        }

        FinalGrade withExam = finalGrade.recordExam(request.examGrade());
        FinalGrade saved = finalGradeRepository.save(withExam);

        log.info("Exam grade recorded for studentCourseSubject: {} result: {}",
                request.studentCourseSubjectId(), saved.getStatus());

        return mapper.toFinalGradeResponse(saved);
    }
}

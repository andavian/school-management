package org.school.management.grades.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.school.management.grades.application.dto.response.EvaluationResponse;
import org.school.management.grades.application.dto.response.FinalGradeResponse;
import org.school.management.grades.application.dto.response.PeriodGradeResponse;
import org.school.management.grades.domain.model.Evaluation;
import org.school.management.grades.domain.model.FinalGrade;
import org.school.management.grades.domain.model.PeriodGrade;

@Mapper(componentModel = "spring")
public interface GradesApplicationMapper {

    @Mapping(target = "evaluationId",
            expression = "java(evaluation.getEvaluationId().value())")
    @Mapping(target = "studentCourseSubjectId",
            expression = "java(evaluation.getStudentCourseSubjectId().value())")
    @Mapping(target = "periodId",
            expression = "java(evaluation.getPeriodId().value())")
    @Mapping(target = "evaluationTypeId",
            expression = "java(evaluation.getEvaluationTypeId().value())")
    @Mapping(target = "isPassed",
            expression = "java(evaluation.isPassed())")
    EvaluationResponse toEvaluationResponse(Evaluation evaluation);

    @Mapping(target = "periodGradeId",
            expression = "java(periodGrade.getPeriodGradeId().value())")
    @Mapping(target = "studentCourseSubjectId",
            expression = "java(periodGrade.getStudentCourseSubjectId().value())")
    @Mapping(target = "periodId",
            expression = "java(periodGrade.getPeriodId().value())")
    PeriodGradeResponse toPeriodGradeResponse(PeriodGrade periodGrade);

    @Mapping(target = "finalGradeId",
            expression = "java(finalGrade.getFinalGradeId().value())")
    @Mapping(target = "studentCourseSubjectId",
            expression = "java(finalGrade.getStudentCourseSubjectId().value())")
    @Mapping(target = "academicYearId",
            expression = "java(finalGrade.getAcademicYearId().value())")
    @Mapping(target = "registryId",
            expression = "java(finalGrade.getRegistryId() != null ? finalGrade.getRegistryId().value() : null)")
    FinalGradeResponse toFinalGradeResponse(FinalGrade finalGrade);
}

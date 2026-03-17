package org.school.management.grades.infrastructure.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.school.management.grades.application.dto.request.CreateEvaluationRequest;
import org.school.management.grades.application.dto.request.GradeEvaluationRequest;
import org.school.management.grades.application.dto.request.RecordExamGradeRequest;
import org.school.management.grades.application.dto.response.EvaluationResponse;
import org.school.management.grades.application.dto.response.FinalGradeResponse;
import org.school.management.grades.application.dto.response.PeriodGradeResponse;
import org.school.management.grades.infrastructure.web.dto.GradesWebDto;

@Mapper(componentModel = "spring")
public interface GradesWebMapper {

    // ── Request web → application ──────────────────────────

    CreateEvaluationRequest toApplicationRequest(
            GradesWebDto.CreateEvaluationWebRequest webRequest);

    GradeEvaluationRequest toApplicationRequest(
            GradesWebDto.GradeEvaluationWebRequest webRequest);

    RecordExamGradeRequest toApplicationRequest(
            GradesWebDto.RecordExamGradeWebRequest webRequest);

    // ── Response application → web ─────────────────────────

    @Mapping(target = "isPassed", expression = "java(response.isPassed())")
    GradesWebDto.EvaluationWebResponse toWebResponse(EvaluationResponse response);

    GradesWebDto.PeriodGradeWebResponse toWebResponse(PeriodGradeResponse response);

    GradesWebDto.FinalGradeWebResponse toWebResponse(FinalGradeResponse response);
}
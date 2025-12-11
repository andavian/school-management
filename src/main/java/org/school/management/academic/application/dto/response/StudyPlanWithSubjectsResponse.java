package org.school.management.academic.application.dto.response;

public record StudyPlanWithSubjectsResponse (
        StudyPlanResponse studyPlan,
        java.util.List<SubjectResponse> subjects,
        Integer totalSubjects
){

}

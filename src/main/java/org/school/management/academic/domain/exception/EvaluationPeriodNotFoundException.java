package org.school.management.academic.domain.exception;

public class EvaluationPeriodNotFoundException extends AcademicDomainException {
    public EvaluationPeriodNotFoundException(String message) {
        super(message);
    }


    public static EvaluationPeriodNotFoundException byId(String id) {
        return new EvaluationPeriodNotFoundException("Evaluation period not found with id: " + id);
    }
}

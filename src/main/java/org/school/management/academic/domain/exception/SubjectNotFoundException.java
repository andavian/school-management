package org.school.management.academic.domain.exception;

import org.school.management.academic.domain.valueobject.ids.SubjectId;

public class SubjectNotFoundException extends RuntimeException {
    public SubjectNotFoundException(SubjectId id) {
        super("Subject not found: " + id);
    }

    public SubjectNotFoundException(String code) {
        super("Subject not found with code: " + code);
    }
}

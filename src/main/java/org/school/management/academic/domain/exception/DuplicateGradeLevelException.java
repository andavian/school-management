package org.school.management.academic.domain.exception;

public class DuplicateGradeLevelException extends RuntimeException {
    public DuplicateGradeLevelException(int year, int yearLevel, String division) {
        super(String.format("Grade level already exists: %d° %s in year %d",
                yearLevel, division, year));
    }
}

package org.school.management.academic.domain.valueobject;

public record Year(int value) {

    public static final int MIN_YEAR = 2000;
    public static final int MAX_YEAR = 2100;

    public Year {
        if (value < MIN_YEAR || value > MAX_YEAR) {
            throw new IllegalArgumentException(String.format(
                    "Academic year must be between %d and %d", MIN_YEAR, MAX_YEAR));
        }
    }

    public static Year of(int value) {
        return new Year(value);
    }

    public static Year current() {
        return new Year(java.time.Year.now().getValue());
    }

    public Year next() {
        return new Year(value + 1);
    }

    public Year previous() {
        return new Year(value - 1);
    }
}

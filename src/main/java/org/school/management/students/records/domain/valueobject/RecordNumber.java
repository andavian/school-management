package org.school.management.students.records.domain.valueobject;

import java.util.regex.Pattern;

public record RecordNumber(String value) {
    private static final Pattern PATTERN = Pattern.compile("^LEG-\\d{4}-\\d{6}$");

    public RecordNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Record number cannot be null or empty");
        }

        if (!PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "Invalid record number format. Expected: LEG-YYYY-NNNNNN, got: " + value
            );
        }
    }

    public static RecordNumber of(String value) {
        return new RecordNumber(value);
    }

    public static RecordNumber create(int year, int sequence) {
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("Year must be between 2000 and 2100");
        }

        if (sequence < 1 || sequence > 999999) {
            throw new IllegalArgumentException("Sequence must be between 1 and 999999");
        }

        String formatted = String.format("LEG-%04d-%06d", year, sequence);
        return new RecordNumber(formatted);
    }

    public int extractYear() {
        String yearPart = value.substring(4, 8);
        return Integer.parseInt(yearPart);
    }

    public int extractSequence() {
        String sequencePart = value.substring(9, 15);
        return Integer.parseInt(sequencePart);
    }
}
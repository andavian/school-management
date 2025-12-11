package org.school.management.students.domain.valueobject;


import lombok.Value;

@Value
public class RecordNumber {
    String value;  // LEG-2024-001234

    public static RecordNumber of(String value) {
        if (!value.matches("LEG-\\d{4}-\\d{6}")) {
            throw new IllegalArgumentException("Invalid record number format");
        }
        return new RecordNumber(value);
    }

    public static RecordNumber generate(int year, int sequence) {
        String value = String.format("LEG-%d-%06d", year, sequence);
        return new RecordNumber(value);
    }
}
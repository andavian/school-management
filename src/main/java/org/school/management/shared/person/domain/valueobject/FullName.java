package org.school.management.shared.person.domain.valueobject;


import java.util.Arrays;
import java.util.stream.Collectors;



public record FullName (
        String firstName,
        String lastName){


    public static FullName of(String firstName, String lastName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
        return new FullName(
                capitalize(firstName.trim()),
                capitalize(lastName.trim())
        );
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getLastNameFirst() {
        return lastName + ", " + firstName;
    }

    private static String capitalize(String str) {
        return Arrays.stream(str.split("\\s+"))
                .map(word -> word.substring(0, 1).toUpperCase() +
                        word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
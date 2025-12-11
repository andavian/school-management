package org.school.management.course.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.school.management.shared.person.domain.valueobject.DNI;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.students.domain.valueobject.StudentId;
import org.school.management.students.domain.valueobject.GradeLevel;
import org.school.management.students.domain.valueobject.Division;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Student {

    private final StudentId studentId;
    private final UserId userId;
    private final DNI dni;
    private final String firstName;
    private final String lastName;
    private final GradeLevel grade;
    private final Division division;
    private final String parentEmail;
    private final LocalDateTime createdAt;

    public static Student create(
            StudentId studentId,
            UserId userId,
            DNI dni,
            String firstName,
            String lastName,
            GradeLevel grade,
            Division division,
            String parentEmail,
            LocalDateTime createdAt
    ) {
        if (studentId == null || userId == null || dni == null) {
            throw new IllegalArgumentException("StudentId, UserId y DNI son obligatorios");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("FirstName es obligatorio");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("LastName es obligatorio");
        }
        return new Student(
                studentId,
                userId,
                dni,
                firstName.trim(),
                lastName.trim(),
                grade,
                division,
                parentEmail,
                createdAt != null ? createdAt : LocalDateTime.now()
        );
    }
}
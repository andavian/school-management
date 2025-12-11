
package org.school.management.shared.person.domain.model;

import org.school.management.shared.person.domain.valueobject.*;

import java.time.LocalDate;

public record Person(
        PersonId personId,
        FullName fullName,
        DNI dni,
        LocalDate birthDate,
        Gender gender,
        Nationality nationality,
        PhoneNumber phoneNumber,
        Email email
) {}
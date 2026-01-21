
package org.school.management.shared.person.domain.model;

import org.school.management.shared.person.domain.valueobject.*;

import java.time.LocalDate;

public record Person(
        PersonId personId,
        FullName fullName,
        Dni dni,
        LocalDate birthDate,
        Gender gender,
        BirthPlaceId birthPlaceId,
        Nationality nationality,
        Address address,
        ResidencePlaceId residencePlaceId,
        PhoneNumber phoneNumber,
        Email email
) {}
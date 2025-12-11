package org.school.management.students.domain.model;

import lombok.Builder;
import lombok.Value;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.person.domain.valueobject.DNI;
import org.school.management.shared.person.domain.valueobject.Email;
import org.school.management.shared.person.domain.valueobject.FullName;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
@Builder
public class Parent {
    ParentId parentId;
    UserId userId;

    FullName fullName;
    DNI dni;
    LocalDate birthDate;
    Gender gender;
    String nationality;

    Email email;
    String phone;
    String phoneAlt;
    Address address;

    String occupation;
    String workplace;
    String workplacePhone;

    boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    UserId createdBy;

    public static Parent createNew(
            UserId userId,
            FullName fullName,
            DNI dni,
            Email email,
            String phone,
            Address address,
            UserId createdBy
    ) {
        return Parent.builder()
                .parentId(ParentId.generate())
                .userId(userId)
                .fullName(fullName)
                .dni(dni)
                .email(email)
                .phone(phone)
                .address(address)
                .nationality("Argentina")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();
    }
}

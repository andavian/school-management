package org.school.management.students.personal.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.school.management.auth.infra.persistence.entity.UserEntity;
import org.school.management.geography.infra.persistence.entity.PlaceEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "student_personal_data")
public class StudentPersonalDataEntity {

    @Id
    @Column(name = "student_id", updatable = false, nullable = false)
    private UUID studentId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    // Datos personales
    @Column(name = "dni", nullable = false, unique = true)
    private String dni;

    @Column(name = "cuil", nullable = false, unique = true)
    private String cuil;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "birth_place_id", nullable = false)
    private UUID birthPlaceId;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private GenderEntity gender;

    @Column(name = "nationality", nullable = false)
    private String nationality;

    // Contacto
    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "address_street", nullable = false)
    private String addressStreet;

    @Column(name = "address_number", nullable = false)
    private String addressNumber;

    @Column(name = "address_floor")
    private String addressFloor;

    @Column(name = "address_apartment")
    private String addressApartment;

    @Column(name = "residence_place_id", nullable = false)
    private UUID residencePlaceId;

    @Column(name = "postal_code")
    private String postalCode;

    // Auditoría
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;


}
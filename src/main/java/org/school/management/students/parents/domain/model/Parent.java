package org.school.management.students.parents.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.geography.domain.valueobject.PlaceId;
import org.school.management.shared.person.domain.valueobject.*;
import org.school.management.students.parents.domain.exception.InvalidParentDataException;
import org.school.management.students.parents.domain.valueobject.ParentId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Agregado Root: Padre / Tutor
 *
 * Entidad global — un padre puede tener hijos en distintas escuelas.
 * Se identifica por DNI (único global).
 * Tiene su propio User para login con rol PARENT.
 *
 * Reglas de negocio:
 * - DNI es único e inmutable
 * - Email es obligatorio — necesario para notificaciones y credenciales
 * - La relación con estudiantes se gestiona desde StudentParent
 */
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Parent {

    // Identidad
    @EqualsAndHashCode.Include
    private final ParentId parentId;
    private final UserId userId;

    // Datos personales
    private final Dni dni;
    private final Cuil cuil;
    private FullName fullName;
    private LocalDate birthDate;
    private Gender gender;
    private Nationality nationality;

    // Contacto — email obligatorio para padres
    private Email email;
    private PhoneNumber phone;
    private PhoneNumber phoneAlt;

    // Domicilio
    private Address address;

    // Información laboral (opcional)
    private String occupation;
    private String workplace;
    private PhoneNumber workplacePhone;

    // Estado
    @Builder.Default
    private boolean isActive = true;

    // Auditoría
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    private final UserId createdBy;

    // ============ Domain Logic ============

    /**
     * Actualiza los datos de contacto del padre.
     */
    public void updateContactInfo(
            Email email,
            PhoneNumber phone,
            PhoneNumber phoneAlt,
            Address address) {

        Objects.requireNonNull(email, "Email cannot be null for a parent");
        Objects.requireNonNull(phone, "Phone cannot be null for a parent");

        this.email = email;
        this.phone = phone;
        this.phoneAlt = phoneAlt;
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Actualiza los datos personales del padre.
     */
    public void updatePersonalInfo(
            FullName fullName,
            LocalDate birthDate,
            Gender gender,
            Nationality nationality) {

        Objects.requireNonNull(fullName, "FullName cannot be null");

        this.fullName = fullName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.nationality = nationality;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Actualiza la información laboral.
     */
    public void updateWorkInfo(
            String occupation,
            String workplace,
            PhoneNumber workplacePhone) {

        this.occupation = occupation;
        this.workplace = workplace;
        this.workplacePhone = workplacePhone;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Desactiva al padre — no elimina físicamente.
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Reactiva al padre.
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    // Factory method
    public static Parent create(ParentBuilder builder) {
        Objects.requireNonNull(builder.parentId,  "ParentId cannot be null");
        Objects.requireNonNull(builder.userId,    "UserId cannot be null");
        Objects.requireNonNull(builder.dni,       "Dni cannot be null");
        Objects.requireNonNull(builder.cuil,      "Cuil cannot be null");   // ← NUEVO
        Objects.requireNonNull(builder.fullName,  "FullName cannot be null");
        Objects.requireNonNull(builder.email,     "Email is mandatory for parents");
        Objects.requireNonNull(builder.phone,     "Phone is mandatory for parents");
        Objects.requireNonNull(builder.createdBy, "CreatedBy cannot be null");

        if (builder.birthDate != null && builder.birthDate.isAfter(LocalDate.now())) {
            throw new InvalidParentDataException("Birth date cannot be in the future");
        }

        return builder.build();
    }
}
package org.school.management.students.personal.infrastructure.persistence.mapper;

import org.mapstruct.*;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.geography.domain.valueobject.PlaceId;
import org.school.management.shared.person.domain.valueobject.*;
import org.school.management.students.personal.domain.model.StudentPersonalData;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.school.management.students.personal.infrastructure.persistence.entity.StudentPersonalDataEntity;

/**
 * PersistenceMapper MapStruct: StudentPersonalData (domain) ↔ StudentPersonalDataEntity (JPA).
 *
 * Estrategia para VOs compuestos:
 * — toEntity():  los VOs del dominio tienen getters simples → MapStruct los aplana sin problema.
 * — toDomain():  FullName y Address necesitan múltiples columnas → se construyen en @AfterMapping.
 *
 * Renombrado de StudentPersonalDataMapper → StudentPersonalDataPersistenceMapper
 * para respetar la convención del proyecto (distinguir de application y web mappers).
 *
 * NO incluye INSTANCE estático — usa componentModel = "spring" (bean inyectado por Spring).
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface StudentPersonalDataPersistenceMapper {

    // ── Domain → Entity (toEntity) ────────────────────────────────────────
    // MapStruct aplana los VOs usando sus getters encadenados (source = "vo.campo").

    @Mapping(source = "studentId.value",          target = "studentId")
    @Mapping(source = "userId.value",             target = "userId")
    @Mapping(source = "dni.value",                target = "dni")
    @Mapping(source = "cuil.value",               target = "cuil")
    @Mapping(source = "fullName.firstName",       target = "firstName")
    @Mapping(source = "fullName.lastName",        target = "lastName")
    @Mapping(source = "birthPlaceId.value",       target = "birthPlaceId")
    @Mapping(source = "residencePlaceId.value",   target = "residencePlaceId")
    @Mapping(source = "gender",                   target = "gender")   // Gender → Gender (mismo enum)
    @Mapping(source = "nationality.value",        target = "nationality")
    @Mapping(source = "phone.value",              target = "phone")
    @Mapping(source = "email.value",              target = "email")
    @Mapping(source = "address.street",           target = "addressStreet")
    @Mapping(source = "address.number",           target = "addressNumber")
    @Mapping(source = "address.floor",            target = "addressFloor")
    @Mapping(source = "address.apartment",        target = "addressApartment")
    @Mapping(source = "address.postalCode",       target = "postalCode")
    // address.placeId.value → residencePlaceId ya mapeado arriba desde residencePlaceId.value
    // createdBy viene del dominio como UserId
    @Mapping(source = "createdBy.value",          target = "createdBy")
    @Mapping(source = "createdAt",                target = "createdAt")
    @Mapping(source = "updatedAt",                target = "updatedAt")
    StudentPersonalDataEntity toEntity(StudentPersonalData domain);

    // ── Entity → Domain (toDomain) ────────────────────────────────────────
    // Los campos simples se mapean automáticamente.
    // FullName y Address (VOs compuestos) se construyen en @AfterMapping.

    @Mapping(target = "studentId",       expression = "java(StudentPersonalDataId.of(entity.getStudentId()))")
    @Mapping(target = "userId",          expression = "java(UserId.from(entity.getUserId()))")
    @Mapping(target = "dni",             expression = "java(Dni.of(entity.getDni()))")
    @Mapping(target = "cuil",            expression = "java(Cuil.of(entity.getCuil()))")
    @Mapping(target = "birthPlaceId",    expression = "java(PlaceId.of(entity.getBirthPlaceId()))")
    @Mapping(target = "residencePlaceId",expression = "java(PlaceId.of(entity.getResidencePlaceId()))")
    @Mapping(target = "gender",          source = "gender")   // Gender → Gender (mismo enum)
    @Mapping(target = "nationality",     expression = "java(Nationality.of(entity.getNationality()))")
    @Mapping(target = "phone",           expression = "java(entity.getPhone() != null ? PhoneNumber.of(entity.getPhone()) : null)")
    @Mapping(target = "email",           expression = "java(entity.getEmail() != null ? Email.of(entity.getEmail()) : null)")
    @Mapping(target = "createdBy",       expression = "java(UserId.from(entity.getCreatedBy()))")
    @Mapping(target = "createdAt",       source = "createdAt")
    @Mapping(target = "updatedAt",       source = "updatedAt")
    // FullName y Address ignorados aquí — los completa @AfterMapping
    @Mapping(target = "fullName",        ignore = true)
    @Mapping(target = "address",         ignore = true)
    StudentPersonalData toDomain(StudentPersonalDataEntity entity);

    /**
     * Construye los VOs compuestos (FullName y Address) después del mapeo automático.
     * Se ejecuta automáticamente al final de toDomain().
     *
     * @MappingTarget permite modificar el builder en curso — requiere que
     * StudentPersonalData use @Builder de Lombok (que ya usa).
     *
     * IMPORTANTE: como StudentPersonalData es inmutable post-build, usamos
     * el patrón de que toDomain() devuelva el objeto ya construido y este
     * método actúa sobre el @MappingTarget del builder implícito de MapStruct.
     *
     * Nota: para clases @Builder de Lombok, MapStruct genera internamente un builder
     * y llama a build() al final. El @AfterMapping recibe el objeto ya construido,
     * pero como phone/email/address/fullName son campos del builder (no final con setter),
     * necesitamos un enfoque alternativo: usar @AfterMapping sobre la entidad
     * y reconstruir manualmente los campos que MapStruct no puede resolver.
     *
     * Solución adoptada: default method que recibe la entidad y retorna el dominio completo,
     * delegando en el método generado para los campos simples y completando los compuestos.
     */
    @AfterMapping
    default void buildCompositeValueObjects(
            StudentPersonalDataEntity entity,
            @MappingTarget StudentPersonalData.StudentPersonalDataBuilder builder) {

        // FullName — construido desde firstName + lastName de la entidad
        if (entity.getFirstName() != null && entity.getLastName() != null) {
            builder.fullName(FullName.of(entity.getFirstName(), entity.getLastName()));
        }

        // Address — construida desde los campos aplanados de la entidad
        // residencePlaceId en Address es el PlaceId del domicilio (columna residence_place_id)
        if (entity.getAddressStreet() != null
                && entity.getAddressNumber() != null
                && entity.getResidencePlaceId() != null) {
            builder.address(new Address(
                    entity.getAddressStreet(),
                    entity.getAddressNumber(),
                    entity.getAddressFloor(),       // nullable
                    entity.getAddressApartment(),   // nullable
                    PlaceId.of(entity.getResidencePlaceId()),
                    entity.getPostalCode()           // nullable
            ));
        }
    }
}
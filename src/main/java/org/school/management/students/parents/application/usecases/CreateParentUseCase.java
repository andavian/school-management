package org.school.management.students.parents.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.model.Role;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.PlainPassword;
import org.school.management.auth.domain.valueobject.RoleName;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.geography.domain.valueobject.PlaceId;
import org.school.management.shared.person.domain.valueobject.*;
import org.school.management.students.parents.application.dto.request.CreateParentRequest;
import org.school.management.students.parents.application.dto.response.ParentResponse;
import org.school.management.students.parents.application.mapper.ParentApplicationMapper;
import org.school.management.students.parents.domain.exception.ParentAlreadyExistsException;
import org.school.management.students.parents.domain.model.Parent;
import org.school.management.students.parents.domain.repository.ParentRepository;
import org.school.management.students.parents.domain.valueobject.ParentId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

/**
 * Crea un nuevo padre/tutor en el sistema.
 *
 * Flujo:
 * 1. Validar unicidad DNI y email
 * 2. Generar password aleatorio seguro
 * 3. Crear User con rol PARENT
 * 4. Crear Parent
 *
 * Password inicial: aleatorio seguro — pendiente email service para notificación.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CreateParentUseCase {

    private final ParentRepository parentRepository;
    private final UserRepository userRepository;
    private final HashedPassword.PasswordEncoder passwordEncoder;
    private final ParentApplicationMapper mapper;

    public ParentResponse execute(CreateParentRequest request, UUID createdByUserId) {
        log.info("Creating parent with DNI: {}", request.dni());

        // ── Paso 1: Validar unicidad ───────────────────────────────────────
        Dni dni = Dni.of(request.dni());
        Email email = Email.of(request.email());

        if (parentRepository.existsByDni(dni)) {
            throw ParentAlreadyExistsException.withDni(request.dni());
        }
        if (parentRepository.existsByEmail(email)) {
            throw ParentAlreadyExistsException.withEmail(request.email());
        }

        // ── Paso 2: Generar password aleatorio seguro ─────────────────────
        String rawPassword = generateSecurePassword();
        PlainPassword plainPassword = PlainPassword.of(rawPassword);

        // ── Paso 3: Crear User con rol PARENT ─────────────────────────────
        Role parentRole = Role.create(RoleName.parent());
        User user = User.create(dni, plainPassword, Set.of(parentRole), passwordEncoder);
        User savedUser = userRepository.save(user);
        UserId userId = savedUser.getUserId();

        log.debug("User created for parent — userId: {}, DNI: {}", userId.value(), request.dni());

        // ── Paso 4: Crear Parent ───────────────────────────────────────────
        ParentId parentId = ParentId.generate();
        UserId createdBy = UserId.from(createdByUserId);

        Address address = buildAddress(request);

        Parent parent = Parent.create(
                Parent.builder()
                        .parentId(parentId)
                        .userId(userId)
                        .dni(dni)
                        .fullName(FullName.of(request.firstName(), request.lastName()))
                        .birthDate(request.birthDate())
                        .gender(request.gender() != null
                                ? Gender.valueOf(request.gender()) : null)
                        .nationality(request.nationality() != null
                                ? Nationality.of(request.nationality()) : null)
                        .email(email)
                        .phone(PhoneNumber.of(request.phone()))
                        .phoneAlt(request.phoneAlt() != null
                                ? PhoneNumber.of(request.phoneAlt()) : null)
                        .address(address)
                        .occupation(request.occupation())
                        .workplace(request.workplace())
                        .workplacePhone(request.workplacePhone() != null
                                ? PhoneNumber.of(request.workplacePhone()) : null)
                        .createdBy(createdBy)
        );

        Parent saved = parentRepository.save(parent);

        // TODO: enviar email con credenciales al padre
        log.warn("Parent created — pending email notification for credentials. parentId: {}",
                parentId.value());

        log.info("Parent created successfully — id: {}, DNI: {}",
                parentId.value(), request.dni());

        return mapper.toParentResponse(saved);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private String generateSecurePassword() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[12];
        random.nextBytes(bytes);
        // Garantizar que cumpla los requisitos de PlainPassword
        String base = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        // Agregar upper, lower, digit y special para cumplir validación
        return base.substring(0, 8) + "Aa1!";
    }

    private Address buildAddress(CreateParentRequest request) {
        if (request.addressStreet() == null || request.residencePlaceId() == null) {
            return null;
        }
        return new Address(
                request.addressStreet(),
                request.addressNumber(),
                request.addressFloor(),
                request.addressApartment(),
                PlaceId.of(request.residencePlaceId()),
                request.postalCode()
        );
    }
}
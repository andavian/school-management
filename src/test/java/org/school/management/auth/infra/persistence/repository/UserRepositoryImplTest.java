/*
package org.school.management.auth.infra.persistence.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.auth.infra.persistence.adapters.UserRepositoryImpl;
import org.school.management.auth.infra.persistence.entity.UserEntity;
import org.school.management.auth.infra.persistence.mappers.AuthPersistenceMapper;
import org.school.management.shared.person.domain.valueobject.DNI;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock
    private UserJpaRepository jpaRepository;

    @Mock
    private AuthPersistenceMapper mapper;

    private UserRepositoryImpl userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryImpl(jpaRepository, mapper);
    }

    @Test
    void save_WithValidUser_ShouldReturnSavedUser() {
        // Arrange
        User user = mock(User.class);
        UserEntity entity = mock(UserEntity.class);
        UserEntity savedEntity = mock(UserEntity.class);
        User savedUser = mock(User.class);

        when(user.getDni()).thenReturn(DNI.of("12345678"));
        when(mapper.toEntity(user)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedUser);
        when(savedUser.getUserId()).thenReturn(UserId.generate());
        when(savedUser.getDni()).thenReturn(DNI.of("12345678"));

        // Act
        User result = userRepository.save(user);

        // Assert
        assertNotNull(result);
        verify(jpaRepository).save(entity);
        verify(mapper).toEntity(user);
        verify(mapper).toDomain(savedEntity);
    }

    @Test
    void findByDni_WithExistingDni_ShouldReturnUser() {
        // Arrange
        DNI dni = DNI.of("12345678");
        UserEntity entity = mock(UserEntity.class);
        User user = mock(User.class);

        when(jpaRepository.findByDni(dni.getValue())).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(user);

        // Act
        Optional<User> result = userRepository.findByDni(dni);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(jpaRepository).findByDni(dni.getValue());
    }

    @Test
    void findByDni_WithNonExistingDni_ShouldReturnEmpty() {
        // Arrange
        DNI dni = DNI.of("99999999");

        when(jpaRepository.findByDni(dni.getValue())).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userRepository.findByDni(dni);

        // Assert
        assertFalse(result.isPresent());
        verify(jpaRepository).findByDni(dni.getValue());
    }

    @Test
    void existsByDni_WithExistingDni_ShouldReturnTrue() {
        // Arrange
        DNI dni = DNI.of("12345678");

        when(jpaRepository.existsByDni(dni.getValue())).thenReturn(true);

        // Act
        boolean result = userRepository.existsByDni(dni);

        // Assert
        assertTrue(result);
        verify(jpaRepository).existsByDni(dni.getValue());
    }

    @Test
    void findByRole_WithExistingRole_ShouldReturnUserList() {
        // Arrange
        String roleName = "STUDENT";
        List<UserEntity> entities = Arrays.asList(
                mock(UserEntity.class),
                mock(UserEntity.class)
        );

        when(jpaRepository.findByRoleName(roleName)).thenReturn(entities);
        when(mapper.toDomain(any(UserEntity.class))).thenReturn(mock(User.class));

        // Act
        List<User> result = userRepository.findByRole(roleName);

        // Assert
        assertEquals(2, result.size());
        verify(jpaRepository).findByRoleName(roleName);
    }

    @Test
    void findActiveUsers_ShouldReturnActiveUsersList() {
        // Arrange
        List<UserEntity> entities = Arrays.asList(
                mock(UserEntity.class),
                mock(UserEntity.class),
                mock(UserEntity.class)
        );

        when(jpaRepository.findByIsActiveTrue()).thenReturn(entities);
        when(mapper.toDomain(any(UserEntity.class))).thenReturn(mock(User.class));

        // Act
        List<User> result = userRepository.findActiveUsers();

        // Assert
        assertEquals(3, result.size());
        verify(jpaRepository).findByIsActiveTrue();
    }

    @Test
    void countByRole_ShouldReturnCorrectCount() {
        // Arrange
        String roleName = "TEACHER";

        when(jpaRepository.countByRoleName(roleName)).thenReturn(15L);

        // Act
        long result = userRepository.countByRole(roleName);

        // Assert
        assertEquals(15L, result);
        verify(jpaRepository).countByRoleName(roleName);
    }
}
*/

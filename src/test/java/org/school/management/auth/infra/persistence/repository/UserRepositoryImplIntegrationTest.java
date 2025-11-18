package org.school.management.auth.infra.persistence.repository;

import org.junit.jupiter.api.Test;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.PlainPassword;
import org.school.management.auth.domain.valueobject.RoleName;
import org.school.management.auth.infra.persistence.adapters.UserRepositoryImpl;
import org.school.management.auth.infra.persistence.mappers.AuthPersistenceMapperImpl;
import org.school.management.shared.domain.valueobjects.DNI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import({UserRepositoryImpl.class, AuthPersistenceMapperImpl.class})
class UserRepositoryImplIntegrationTest {

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private HashedPassword.PasswordEncoder passwordEncoder;

    @Test
    void save_AndFindByDni_ShouldWorkCorrectly() {
        // Arrange
        User user = User.create(
                DNI.of("12345678"),
                PlainPassword.of("Test123!"),
                Set.of(RoleName.student()),
                passwordEncoder
        );

        // Act
        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findByDni(DNI.of("12345678"));

        // Assert
        assertNotNull(savedUser);
        assertTrue(foundUser.isPresent());
        assertEquals("12345678", foundUser.get().getDni().getValue());
    }

    @Test
    void findByRole_ShouldReturnUsersWithRole() {
        // Arrange
        User student1 = createAndSaveStudent("11111111");
        User student2 = createAndSaveStudent("22222222");
        User teacher = createAndSaveTeacher("33333333");

        // Act
        List<User> students = userRepository.findByRole("STUDENT");
        List<User> teachers = userRepository.findByRole("TEACHER");

        // Assert
        assertEquals(2, students.size());
        assertEquals(1, teachers.size());
    }

    @Test
    void findActiveUsers_ShouldReturnOnlyActiveUsers() {
        // Arrange
        User activeUser = createAndSaveStudent("44444444");
        User inactiveUser = createAndSaveStudent("55555555");
        inactiveUser.deactivate();
        userRepository.save(inactiveUser);

        // Act
        List<User> activeUsers = userRepository.findActiveUsers();
        List<User> inactiveUsers = userRepository.findInactiveUsers();

        // Assert
        assertTrue(activeUsers.size() >= 1);
        assertTrue(inactiveUsers.size() >= 1);
    }

    private User createAndSaveStudent(String dni) {
        User user = User.create(
                DNI.of(dni),
                PlainPassword.of("Test123!"),
                Set.of(RoleName.student()),
                passwordEncoder
        );
        return userRepository.save(user);
    }

    private User createAndSaveTeacher(String dni) {
        User user = User.create(
                DNI.of(dni),
                PlainPassword.of("Test123!"),
                Set.of(RoleName.teacher()),
                passwordEncoder
        );
        return userRepository.save(user);
    }
}

package org.school.management.auth.infra.web;

import lombok.RequiredArgsConstructor;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.valueobject.RoleName;
import org.school.management.shared.domain.valueobjects.Email;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;

    @PostMapping
    public ResponseEntity<User> register(@RequestParam String email,
                                         @RequestParam String password,
                                         @RequestParam(defaultValue = "STUDENT") RoleName role) {
        User user = userUseCase.registerUser(new Email(email), password, Set.of(role));
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userUseCase.findAll());
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        userUseCase.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
}

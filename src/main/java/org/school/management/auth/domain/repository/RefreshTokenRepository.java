package org.school.management.auth.domain.repository;

import org.school.management.auth.domain.model.RefreshToken;
import org.school.management.shared.person.domain.valueobject.Dni;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository {

    RefreshToken save(RefreshToken token);

    Optional<RefreshToken> findByTokenHash(String hash);

    List<RefreshToken> findAllByUserDni(Dni dni);

    void revokeAllByUserDni(Dni dni);
}

package org.school.management.auth.infra.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.shared.person.domain.valueobject.DNI;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Cargando usuario por DNI: {}", username);

        try {
            // Username ahora es el DNI
            DNI dni = DNI.of(username);

            return userRepository.findByDni(dni)
                    .orElseThrow(() -> {
                        log.warn("Usuario no encontrado con DNI: {}", username);
                        return new UsernameNotFoundException("Usuario no encontrado con DNI: " + username);
                    });

        } catch (IllegalArgumentException e) {
            log.error("DNI inválido: {}", username);
            throw new UsernameNotFoundException("DNI inválido: " + username);
        }
    }
}

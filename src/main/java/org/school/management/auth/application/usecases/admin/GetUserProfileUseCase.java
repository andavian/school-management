package org.school.management.auth.application.usecases.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.dto.responses.UserResponse;
import org.school.management.auth.application.mappers.AuthApplicationMapper;
import org.school.management.auth.domain.exception.UserNotFoundException;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.UserId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserProfileUseCase {

    private final UserRepository userRepository;
    private final AuthApplicationMapper mapper;

    public UserResponse execute(String userId) {
        UserId userIdVO = UserId.from(userId);

        User user = userRepository.findById(userIdVO)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        return mapper.toUserResponse(user);
    }
}


package org.school.management.auth.application.usecases.admin;

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


package com.yechat.users.user;

import com.yechat.users.exception.BadRequestException;
import com.yechat.users.user.exception.UserNotFoundException;
import com.yechat.users.user.request.UserRequest;
import com.yechat.users.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse getUser(@NonNull Integer id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    public UserResponse createUser(@NonNull UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("User with email " + request.email() + " already exists");
        }
        User user = User.builder()
                .username(request.username())
                .lastname(request.lastname())
                .firstname(request.firstname())
                .email(request.email())
                .build();
        return userMapper.toResponse(userRepository.save(user));
    }
}

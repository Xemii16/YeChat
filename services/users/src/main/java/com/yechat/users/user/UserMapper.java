package com.yechat.users.user;

import com.yechat.users.user.response.UserResponse;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .build();
    }
}

package com.yechat.chats.user.response;

public record UserResponse(
        Integer id,
        String username,
        String firstname,
        String lastname
) {
}

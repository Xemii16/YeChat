package com.yechat.contacts.user;

public record UserResponse(
        Integer id,
        String username,
        String firstname,
        String lastname
) {
}
